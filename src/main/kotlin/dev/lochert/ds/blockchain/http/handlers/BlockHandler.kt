package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.Message
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread

// Partially inspired by ChatGPT
/**
 * Handles `/block` endpoint
 * - GET: Returns all blocks
 * - POST: Accepts a new block proposal (validates and adds it)
 */
open class BlockHandler(val server: Server) : HttpHandler {

    val addressList = server.addressList

    override fun handle(exchange: HttpExchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*")
        println("${addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        when (exchange.requestMethod) {
            "GET" -> handleGet(exchange)
            "POST" -> handlePost(exchange)
            else -> sendResponse(exchange, "Method Not Allowed", 405)
        }
    }

    private fun handleGet(exchange: HttpExchange) {
        val response = Json.encodeToString(server.blockChain.listOfBlocks)
        sendResponse(exchange, response, 200)
    }

    private fun handlePost(exchange: HttpExchange) {

        println("${addressList.ownAddress}\t Handle Post Begin")
        val requestBody = exchange.requestBody.bufferedReader().use { it.readText() }
        println("${addressList.ownAddress}\t Handle Post Request Body")
        val block = try {
            println("${addressList.ownAddress}\t Handle Post Request Decode")
            Json.decodeFromString<Block>(requestBody)
        } catch (e: Exception) {
            sendResponse(exchange, "Invalid JSON format", 400) // Bad Request
            return
        }
        synchronized(server.blockChain) {
            when {
                server.blockChain.listOfBlocks.last().blockHash == block.blockHash -> sendResponse(
                    exchange,
                    Message.blockAlreadyExists,
                    208
                ) // Already Reported

                // Have to use custom response codes because the 400 block throws an exception
                server.blockChain.listOfBlocks.contains(block) -> sendResponse(
                    exchange,
                    Message.OutdatedBlockchain,
                    298 // Custom HTTP Code
                ) // Conflict
                server.blockChain.listOfBlocks.last().blockHash != block.parentHash -> sendResponse(
                    exchange,
                    Message.ParentHashDoesNotMatch,
                    299 //Custom HTTP Code
                )
                else -> {
                    try {
                        server.blockChain.addBlock(block) // Add block
                        println("${addressList.ownAddress}: Adding ${block.content} to blockchain (${server.blockChain.listOfBlocks.map { it.content }})")
                        sendResponse(exchange, Json.encodeToString(Message.blockAlreadyExists), 201) // Created
                        propagateBlock(block)
                        server.transactions.removeTransactions(block.transactions)

                    } catch (e: Exception) {
                        println(
                            "\n" +
                                    "\n" +
                                    "\nPANIC"
                        )
                        e.printStackTrace()
                    }

                }

            }

            println("${addressList.ownAddress}\t Handle Post Request End")
        }
    }

    /**
     * Why is this a thread?
     * Only one instance of a Handler may exist at the same time
     *
     * Hence, if two people want to send a /block, they would be processed sequentially
     * But the second person would have to wait until propagate block is completed, which takes a while
     * Plus it easy to end up in a loop situation, in which the read timeout is hit
     */
    protected fun propagateBlock(block: Block){
        var responseCodes: List<Pair<String, Pair<Int, String>>>
        thread {

            println("${addressList.ownAddress}: Propagating to ${addressList.addressList}")
            responseCodes = addressList.addressList.mapNotNull {
                try {
                    Pair(it.toString(), HttpUtil.sendPostRequest(it.toUrl("block"), Json.encodeToString(block)))
                }
                catch (e:Exception){
                    println("${addressList.ownAddress}): Exception trying to send a block (POST /block) to $it - ${e.javaClass}")
                    e.printStackTrace()
                    null
                }
            }

            println("${addressList.ownAddress}: Response Codes for Block Propagation (${responseCodes.size})")
            responseCodes.forEach { println("\t- $it") }
        }

    }
}