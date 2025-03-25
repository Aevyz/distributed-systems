package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json

class BlocksHandlerHash(val server: Server) : HttpHandler {
    // Send blocks from the blockchain starting with the provided hash
    override fun handle(exchange: HttpExchange) {
        println("Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")

        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size < 5 || !parts[3].equals("from")) {
            sendResponse(exchange, "Invalid request format", 400)
            return
        }
        val hashStr = parts[4]
        if (server.blockChain.doesBlockExist(hashStr)) {
            val subBlockChain = server.blockChain.searchForBlocksFrom(hashStr.toString())
            val response = Json.encodeToString(subBlockChain)
            sendResponse(exchange, response, 200)
        }
        else {
            sendResponse(exchange, "Cannot find hash", 404)
        }
    }
}