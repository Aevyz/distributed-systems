package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.sendResponse
import kotlinx.serialization.json.Json

/**
 * Handles `/control/add-block/{string}` for adding a block based on string content.
 */
class ControlAddHandler(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size < 4) {
            sendResponse(exchange, "Invalid request format", 400)
            return
        }

        val content = parts[3]
        println("Received content: $content")

        val block = blockChain.addBlock(content)
        sendResponse(exchange, Json.encodeToString(block.blockHash), 200)
    }
}