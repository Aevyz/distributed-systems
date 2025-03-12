package dev.lochert.ds.blockchain.Transactions

import kotlinx.serialization.Serializable
@Serializable
class Transaction {
    constructor(sender:String, receiver:String, amount:Double){
        this.sender = sender
        this.receiver = receiver
        this.amount = amount
    }

    val sender:String
    val receiver:String
    val amount:Double

    override fun toString(): String {
        return "Transaction(sender='$sender', receiver='$receiver', amount=$amount)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (sender != other.sender) return false
        if (receiver != other.receiver) return false
        if (amount != other.amount) return false
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + sender.hashCode()
        result = 31 * result + receiver.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }
}