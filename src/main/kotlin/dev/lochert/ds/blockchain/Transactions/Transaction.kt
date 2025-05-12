package dev.lochert.ds.blockchain.Transactions

import dev.lochert.ds.blockchain.Constants.defaultTransactionFee
import dev.lochert.ds.blockchain.pki.RSAKeyPair
import dev.lochert.ds.blockchain.pki.RSAKeyPairs
import kotlinx.serialization.Serializable

@Serializable
class Transaction(
    val sender: String,
    val senderShortName: String? = RSAKeyPairs.getRSAKeyPair(sender)?.name,
    val receiver: String,
    val receiverShortName: String? = RSAKeyPairs.getRSAKeyPair(receiver)?.name,
    val amount: Double,
    val transactionMinerReward: Double = defaultTransactionFee.toDouble(),
    val signature: String
) {
    constructor(
        sender: RSAKeyPair,
        receiver: String,
        amount: Double,
        minerReward: Double = defaultTransactionFee.toDouble()
    ) : this(
        sender.publicKeyToString(),
        sender.name,
        receiver,
        RSAKeyPairs.getRSAKeyPair(receiver)?.name,
        amount,
        minerReward,
        sender.sign(toSigningString(sender, receiver, amount, minerReward))
    ) {
        println(this)
    }

    fun toSigningString() = sender + receiver + amount + transactionMinerReward
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (amount != other.amount) return false
        if (sender != other.sender) return false
        if (receiver != other.receiver) return false
        if (signature != other.signature) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + receiver.hashCode()
        result = 31 * result + signature.hashCode()
        return result
    }

    override fun toString(): String {
        return "Transaction(sender='$sender', senderShortName=$senderShortName, receiver='$receiver', receiverShortName=$receiverShortName, amount=$amount, minerReward=$transactionMinerReward, signature='$signature')"
    }

    // Untested
    fun verify() = verifyTransaction(this)


    // Untested
    companion object {
        fun verifyTransaction(transaction: Transaction) =
            RSAKeyPair.verifySignature(transaction.toSigningString(), transaction.signature, transaction.sender)

        fun toSigningString(sender: String, receiver: String, amount: Double, minerReward: Double) =
            sender + receiver + amount + minerReward

        fun toSigningString(sender: RSAKeyPair, receiver: String, amount: Double, minerReward: Double) =
            toSigningString(sender.publicKeyToString(), receiver, amount, minerReward)
    }




}