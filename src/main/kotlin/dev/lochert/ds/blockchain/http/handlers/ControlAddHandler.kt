package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.http.HttpUtil.sendResponse
import dev.lochert.ds.blockchain.http.server.Server
import kotlinx.serialization.json.Json

// Partially inspired by ChatGPT
/**
 * Handles `/control/add-block/{string}` for adding a block based on string content.
 */
class ControlAddHandler(server: Server) : BlockHandler(server) {
    var counter = 0
    override fun handle(exchange: HttpExchange) {
        val pBlockChain = server.blockChain
        val transactions = server.transactions
        val addressList = server.addressList
        println("${addressList.ownAddress}: Received ${exchange.requestMethod} from ${exchange.remoteAddress} (${exchange.requestURI})")
        val path = exchange.requestURI.path
        val parts = path.split("/")
        val transactionsToBuildBlock: Transactions = transactions

        if (parts.size < 2) {
            sendResponse(exchange, "Invalid request format", 400)
            return
        }


        val content = parts[3].ifEmpty {
            "blockFrom${addressList.ownAddress.port}Number${counter++}"
        }
        println("Received content: $content")

        val block = pBlockChain.addBlock(content, transactionsToBuildBlock)
        sendResponse(exchange, Json.encodeToString(block.blockHash), 200)
        propagateBlock(block)
        // As Block was created, the Transactions used should be removed.
        transactions.removeTransactions(transactionsToBuildBlock.allTransactions())
    }
}