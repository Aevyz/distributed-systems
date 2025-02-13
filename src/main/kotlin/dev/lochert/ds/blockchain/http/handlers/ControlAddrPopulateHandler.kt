package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.sendResponse

// Partially inspired by ChatGPT
class ControlAddrPopulateHandler(val blockChain: BlockChain) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        sendResponse(exchange, "Dummy response for populating address list", 200)
    }
}