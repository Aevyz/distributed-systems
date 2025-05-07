package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.block.balance.getBalances
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json

open class BalanceHandler(val server: Server) : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        println("${server.addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        val balances =
            server.blockChain.getBalances()
        val response = Json.encodeToString(
            balances
        )
        sendResponse(exchange, response, 200)
    }
}
