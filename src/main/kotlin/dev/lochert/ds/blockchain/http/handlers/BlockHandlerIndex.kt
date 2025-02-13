package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.sendResponse
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
/**
 * Handles `/block/index/{index}` for retrieving a block by index.
 */
class BlockHandlerIndex(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size < 4) {
            sendResponse(exchange, "Invalid request", 400)
            return
        }

        val indexStr = parts[3]
        val index = indexStr.toIntOrNull()

        if (index == null || index !in blockChain.listOfBlocks.indices) {
            sendResponse(exchange, "Block not found", 404)
            return
        }

        val response = Json.encodeToString(blockChain.listOfBlocks[index])
        sendResponse(exchange, response, 200)
    }
}