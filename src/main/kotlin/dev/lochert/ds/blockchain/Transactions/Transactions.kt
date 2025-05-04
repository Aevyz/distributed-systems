package dev.lochert.ds.blockchain.Transactions

import kotlinx.serialization.Serializable
import kotlin.random.Random
@Serializable
class Transactions() {
    val listOfTransactions = mutableListOf<Transaction>()

    fun allTransactions():List<Transaction> = listOfTransactions.toList()

    // Add transaction to the transactions list
    fun addTransactionToList(newTransaction: Transaction) {
        if (doesTransactionExist(newTransaction)) {
            println("Can't add a duplicate transaction to list")
        } else {
            println("New transaction in list: " + newTransaction.toString())
            listOfTransactions.add(newTransaction)
        }

    }

    // Add a transaction to the transactions list with provided values
    fun addTransactionToList(sender: String, recipient:String, amount:Double): Transaction {
        val transaction = Transaction(sender, recipient, amount)
        addTransactionToList(transaction)
        return transaction
    }

    // Add a transaction with random values to the transactions list
    fun addRandomTransactionToList(): Transaction {
        val sender = generateAlphanumericString(4)
        val receiver = generateAlphanumericString(4)
        val amount = Random.nextDouble(0.0, 500.0)
        val newTransaction = Transaction(sender, receiver, amount)
        addTransactionToList(newTransaction)
        return newTransaction
    }

    // Create a list of valid characters
    val alphanumeric = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    // Creates a String containing x random characters
    fun generateAlphanumericString(length: Int) : String {
        // The buildString function will create a StringBuilder
        return buildString {
            // We will repeat length times and will append a random character each time
            // This roughly matches how you would do it in plain Java
            repeat(length) { append(alphanumeric.random()) }
        }
    }

    // Checks if transaction is in the transactions list
    fun doesTransactionExist(queryTransaction: Transaction): Boolean {
        listOfTransactions.forEach{
            if (queryTransaction.equals(it)) {
                return true
            }
        }
        return false
    }

    // Removes given transactions from the transactions list
    fun removeTransactions(queryTransactions: List<Transaction>) {
        queryTransactions.forEach{
            if (doesTransactionExist(it)) {
                println("removing transaction: " + queryTransactions.toString())
                listOfTransactions.remove(it)
            } else {
                println("Couldn't delete transaction")
            }
        }
    }

    // Clears transaction list
    fun clearTransactions() {
        println("clearing transactions")
        listOfTransactions.clear()
    }
}