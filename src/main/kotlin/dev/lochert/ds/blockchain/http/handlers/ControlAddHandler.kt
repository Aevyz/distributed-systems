package dev.lochert.ds.blockchain.http.handlers

import com.sun.net.httpserver.HttpExchange
import dev.lochert.ds.blockchain.Constants.transactionsInBlock
import dev.lochert.ds.blockchain.Transactions.Transaction
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.block.balance.BalanceEntry
import dev.lochert.ds.blockchain.block.balance.getBalances
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
        val transactionsToBuildBlock: Transactions = sortActionableTransactions(server.transactions, pBlockChain.getBalances())

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

    // run through all pending transactions and see which can be done without going negative
    fun sortActionableTransactions(transactions: Transactions, balances: List<BalanceEntry>): Transactions {
        val chosenTransactions: Transactions = Transactions()
        var count = 0
        transactions.allTransactions().forEach {transaction ->
            val thereIsSuchAccount = balances.filter { it.name == transaction.senderShortName }.size != 0
            if (count < transactionsInBlock && thereIsSuchAccount && !doBlocksContainTransaction(transaction)) {
                val senderBalance = balances.first() { it.name == transaction.senderShortName }.balance
                val transactionValue = transaction.amount + transaction.transactionMinerReward
                if (transactionValue <= senderBalance) {
                    chosenTransactions.addTransactionToList(transaction)
                    count += 1
                }
            }
        }
        return chosenTransactions
    }

    fun doBlocksContainTransaction(transaction: Transaction): Boolean {
        server.blockChain.allBlocks().forEach {
            if (it.transactions.contains(transaction)) {
                server.transactions.removeTransaction(transaction)
                return true
            }
        }
        return false
    }
}