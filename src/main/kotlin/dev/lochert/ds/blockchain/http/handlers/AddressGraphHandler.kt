package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.Message
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.LastGraph


class AddressGraphHandler(val addressList: AddressList) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("${addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        when (exchange.requestMethod) {
            "GET" -> handleGet(exchange)
            else -> sendResponse(exchange, "Method Not Allowed", 405)
        }
    }

    private fun handleGet(exchange: HttpExchange) {
        try{
            val response = LastGraph.lastGraph
            sendResponse(exchange, response, 200)
        }catch (e:Exception){
            e.printStackTrace()
            sendResponse(exchange, Message.exceptionMessage(e), 400)
        }
    }
}