package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.sendResponse
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
/**
 * Handles `/block` endpoint
 * - GET: Returns all blocks
 * - POST: Accepts a new block proposal (validates and adds it)
 */
class BlockHandler(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        when (exchange.requestMethod) {
            "GET" -> handleGet(exchange)
            "POST" -> handlePost(exchange)
            else -> sendResponse(exchange, "Method Not Allowed", 405)
        }
    }

    private fun handleGet(exchange: HttpExchange) {
        val response = Json.encodeToString(blockChain.listOfBlocks)
        sendResponse(exchange, response, 200)
    }

    private fun handlePost(exchange: HttpExchange) {
        val requestBody = exchange.requestBody.bufferedReader().use { it.readText() }
        val block = try {
            Json.decodeFromString<Block>(requestBody)
        } catch (e: Exception) {
            sendResponse(exchange, "Invalid JSON format", 400) // Bad Request
            return
        }

        when {
            blockChain.listOfBlocks.contains(block) -> sendResponse(
                exchange,
                "Block already exists",
                208
            ) // Already Reported
            blockChain.listOfBlocks.last().blockHash!=block.parentHash -> sendResponse(
                exchange,
                "Parent hash does not match",
                406
            ) // Not Acceptable
            else -> {
                blockChain.addBlock(block) // Add block
                sendResponse(exchange, Json.encodeToString(block.blockHash), 201) // Created
            }
        }
    }
}