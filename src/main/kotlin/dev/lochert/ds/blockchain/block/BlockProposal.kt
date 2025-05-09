package dev.lochert.ds.blockchain.block

import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.block.BlockUtils.Companion.hasLeadingZeroBytes
import dev.lochert.ds.blockchain.block.BlockUtils.Companion.hashByteArray
import dev.lochert.ds.blockchain.pki.RSAKeyPairs

// Used ChatGPT
data class BlockProposal(
    val parentBlockHashHex:String,
    val content:String,
    val transactions: Transactions,
    val miner: String = defaultMinerForThisNodePublicKey,
){
    companion object {
        val defaultMinerForThisNode = (RSAKeyPairs.listOfKeyPairs - RSAKeyPairs.mallory).random()
        val defaultMinerForThisNodePublicKey = defaultMinerForThisNode.publicKeyToString()
    }
    constructor(previousBlock: Block, content: String, transactions: Transactions) : this(previousBlock.blockHash, content, transactions)

    // Required because I am using the toHex function
    @OptIn(ExperimentalStdlibApi::class)
    fun generateBlock(): Block {
        var nonce = 0
        var blockHash: ByteArray

        do {
            nonce++
            val input = parentBlockHashHex + nonce.toString() + content + miner
            blockHash = hashByteArray(input.toByteArray())
            if(nonce%1000000==0){
                println("[Debug] Tried Nonce's: $nonce - ${blockHash[0]} ${blockHash[1]}")
            }
        } while (!hasLeadingZeroBytes(blockHash))

        println("Found Nonce: $nonce, Hash: ${blockHash.toHexString()}")

        return Block(parentBlockHashHex, nonce, content, blockHash.toHexString(), transactions, miner)
    }

}
