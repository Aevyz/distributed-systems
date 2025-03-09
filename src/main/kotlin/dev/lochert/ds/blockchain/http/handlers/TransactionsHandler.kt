package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import kotlinx.serialization.json.Json

class TransactionsHandler(val transactions: Transactions) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")

        val path = exchange.requestURI.path
        val parts = path.split("/")

        if (parts.size != 3) {
            sendResponse(exchange, "Invalid request format", 400)
            return
        }
        if (path.equals("/transactions/all")) {
            println("all transactions")
            val test = transactions.allTransactions()
            val response = Json.encodeToString(test.toString())
            sendResponse(exchange, response, 200)
            return
        }
        if (path.equals("/transactions/create")) {
            println("create transaction")
            //val newTransaction = Transaction("A", "B", 1.5)
            val transaction = transactions.addRandomTransactionToList()
            val response = Json.encodeToString(transaction.toString())
            sendResponse(exchange, response, 200)
            return
        }
        sendResponse(exchange, "Request format not mapped", 400)
    }
}