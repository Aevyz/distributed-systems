package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
class BlockHandlerHash(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size < 4) {
            sendResponse(exchange, "Invalid request", 400)
            return
        }

        val hashStr = parts[3]
        val block = blockChain.listOfBlocks.firstOrNull{it.blockHash==hashStr}
        if(block==null){
            sendResponse(exchange, "Cannot find hash", 404)
        }
        else{
            val response = Json.encodeToString(block)
            sendResponse(exchange, response, 200)
        }
    }
}