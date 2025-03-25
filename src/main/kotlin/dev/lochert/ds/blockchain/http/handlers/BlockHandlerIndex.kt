package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
/**
 * Handles `/block/index/{index}` for retrieving a block by index.
 */
class BlockHandlerIndex(val server: Server) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size < 4) {
            sendResponse(exchange, "Invalid request", 400)
            return
        }

        val indexStr = parts[3]
        val index = indexStr.toIntOrNull()

        if (index == null || index !in server.blockChain.listOfBlocks.indices) {
            sendResponse(exchange, "Block not found", 404)
            return
        }

        val response = Json.encodeToString(server.blockChain.listOfBlocks[index])
        sendResponse(exchange, response, 200)
    }
}