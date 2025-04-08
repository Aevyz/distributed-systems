package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import dev.lochert.ds.blockchain.Transactions.Transaction
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.Message
import kotlinx.serialization.json.Json
import kotlin.concurrent.thread

class TransactionsHandler(val addressList: AddressList, val transactions: Transactions) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*")
        println("${addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        val parts = exchange.requestURI.path.split("/")

        // If somehow query was aimed at it from some other url
        if (parts[1] != "transactions") {
            sendResponse(exchange, "Invalid request format", 400)
            return
        }

        when (exchange.requestMethod) {
            "GET" -> handleGet(exchange)
            "POST" -> handlePost(exchange)
            else -> sendResponse(exchange, "Method Not Allowed", 405)
        }
    }

    private fun handleGet(exchange: HttpExchange) {
        val path = exchange.requestURI.path
        val parts = path.split("/")

        // Returns all transactions
        if (path.equals("/transactions/all")) {
            println("all transactions")
            val test = transactions.allTransactions()
            val response = Json.encodeToString(test)
            sendResponse(exchange, response, 200)
            return
        }
        // If no values are given, then generates a transaction with random values
        if (path.equals("/transactions/create")) {
            println("create transaction")
            //val newTransaction = Transaction("A", "B", 1.5)
            val transaction = transactions.addRandomTransactionToList()
            val response = Json.encodeToString(transaction.toString())
            propagateTransaction(transaction)
            sendResponse(exchange, response, 200)
            return
        }
        // If values are given, then generates a transaction with provided values
        if (parts[2] == "create" && parts.size == 6) {
            val transaction = transactions.addTransactionToList(parts[4], parts[5], parts[6].toDouble())
            val response = Json.encodeToString(transaction.toString())
            propagateTransaction(transaction)
            sendResponse(exchange, response, 200)
            return
        }
        sendResponse(exchange, "Request format not mapped", 400)
    }

    private fun handlePost(exchange: HttpExchange) {

        println("${addressList.ownAddress}\t Handle Post Begin")
        val requestBody = exchange.requestBody.bufferedReader().use { it.readText() }
        println("${addressList.ownAddress}\t Handle Post Request Body")
        val transaction = try {
            println("${addressList.ownAddress}\t Handle Post Request Decode")
            Json.decodeFromString<Transaction>(requestBody)
        } catch (e: Exception) {
            sendResponse(exchange, "Invalid JSON format", 400) // Bad Request
            return
        }
        synchronized(transactions) {
            when {
                transactions.doesTransactionExist(transaction) -> sendResponse(
                    exchange,
                    Message.transactionAlreadyExists,
                    208
                ) // Already in ledger
                else -> {
                    try {
                        transactions.addTransactionToList(transaction) // Add transaction
                        println("${addressList.ownAddress}: Adding $transaction to transactions")
                        sendResponse(
                            exchange,
                            Json.encodeToString(Message.transactionAddedSuccessfully),
                            201
                        ) // Created
                        propagateTransaction(transaction)
                    } catch (e: Exception) {
                        println(
                            "\n" +
                                    "\n" +
                                    "\nPANIC"
                        )
                        e.printStackTrace()
                    }

                }

            }

            println("${addressList.ownAddress}\t Handle Post Request End")
        }
    }

    // Sends out a transaction to other nodes
    private fun propagateTransaction(transaction: Transaction) {
        var responseCodes: List<Pair<String, Pair<Int, String>>> = listOf()
        thread {

            println("${addressList.ownAddress}: Propagating transaction to ${addressList.addressList}")
            responseCodes = addressList.addressList.mapNotNull {
                try {
                    Pair(
                        it.toString(),
                        HttpUtil.sendPostRequest(it.toUrl("transactions/post"), Json.encodeToString(transaction))
                    )
                } catch (e: Exception) {
                    println("${addressList.ownAddress}): Exception trying to send a transaction (POST /transaction) to $it - ${e.javaClass}")
                    e.printStackTrace()
                    null
                }
            }

            println("${addressList.ownAddress}: Response Codes for Transaction Propagation (${responseCodes.size})")
            responseCodes.forEach { println("\t- $it") }
        }

    }
}