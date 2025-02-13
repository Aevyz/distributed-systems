package dev.lochert.ds.blockchain.block

import dev.lochert.ds.blockchain.block.BlockUtils.Companion.hasLeadingZeroBytes
import dev.lochert.ds.blockchain.block.BlockUtils.Companion.hashByteArray

// Used ChatGPT
data class BlockProposal(
    val parentBlockHashHex:String,
    val content:String
){
    constructor(previousBlock: Block, content: String) : this(previousBlock.blockHash, content)

    // Required because I am using the toHex function
    @OptIn(ExperimentalStdlibApi::class)
    fun generateBlock(): Block {
        var nonce = 0
        var blockHash: ByteArray

        do {
            nonce++
            val input = parentBlockHashHex + nonce.toString() + content
            blockHash = hashByteArray(input.toByteArray())
            if(nonce%1000000==0){
                println("[Debug] Tried Nonce's: $nonce - ${blockHash[0]} ${blockHash[1]}")
            }
        } while (!hasLeadingZeroBytes(blockHash))

        println("Found Nonce: $nonce, Hash: ${blockHash.toHexString()}")

        return Block(parentBlockHashHex, nonce, content, blockHash.toHexString())
    }

}
