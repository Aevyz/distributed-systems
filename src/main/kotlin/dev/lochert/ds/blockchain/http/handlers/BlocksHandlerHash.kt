package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
class BlocksHandlerHash(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")

        // Gets query parameters
        val queryParams = exchange.requestURI.getQuery()

        // Checks if the "hash=" parameter is there
        if (!queryParams.contains("hash=")) {
            sendResponse(exchange, "Invalid request", 400)
            return
        }

        // Removes "hash=" from the query parameter to get only the value.
        // Doesn't expect multiple parameters AT THIS POINT
        val hashStr = queryParams.drop(5)

        // Looks if the requested block is in blockChain. If it is, then returns that block and all after that. If not, returns 404
        if(blockChain.doesBlockExist(hashStr.toString())){
            val subBlockChain = blockChain.searchForBlocksFrom(hashStr.toString())
            val response = Json.encodeToString(subBlockChain)
            sendResponse(exchange, response, 200)
        }
        else {
            sendResponse(exchange, "Cannot find hash", 404)
        }
    }
}