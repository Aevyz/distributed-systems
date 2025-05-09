package dev.lochert.ds.blockchain.block

import dev.lochert.ds.blockchain.Transactions.Transaction
import dev.lochert.ds.blockchain.Transactions.Transactions
import dev.lochert.ds.blockchain.pki.RSAKeyPairs
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalStdlibApi::class)
class Block{
    //This private constructor is only relevant for genesis block
    private constructor(){
        parentHash=""
        content="Genesis"
        var tNonce = 0
        var tHash:ByteArray?
        do{
            tHash = BlockUtils.hashByteArray(
                (parentHash+tNonce+content).toByteArray()
            )
            if(BlockUtils.hasLeadingZeroBytes(tHash)){
                break
            }
            tNonce++
        }while(true)

        nonce = tNonce
        blockHash = tHash.toHexString()
        transactions = Transactions().allTransactions()
        miner = RSAKeyPairs.alice.publicKeyToString()
    }

    constructor(
        parentHash: String,
        nonce: Int,
        content: String,
        blockHash: String,
        transactions: Transactions,
        miner: String
    ) {
        assert(parentHash.isNotBlank()){"Parent Hash cannot be empty"}
        assert(isValid(parentHash, nonce, content, blockHash, miner)) { "Block is not valid" }
        this.parentHash = parentHash
        this.nonce=nonce
        this.content=content
        this.blockHash=blockHash
        this.transactions=transactions.allTransactions()
        this.miner = miner

    }
    val parentHash: String
    val nonce:Int
    val content:String
    val blockHash:String
    val transactions:List<Transaction>
    val miner: String

    fun isGenesis() = parentHash.isEmpty()

    override fun toString(): String {
        return "Block(nonce=$nonce, content='$content', parentHash='$parentHash', blockHash='$blockHash', transactions='${transactions}, miner='$miner')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (nonce != other.nonce) return false
        if (parentHash != other.parentHash) return false
        if (content != other.content) return false
        if (blockHash != other.blockHash) return false
        if (transactions != other.transactions) return false
        if (miner != other.miner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce
        result = 31 * result + parentHash.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + blockHash.hashCode()
        result = 31 * result + miner.hashCode()
        return result
    }


    companion object{
        val genesisNode = Block()
        fun isValid(parentHash: String, nonce: Int, content: String, blockHash: String, miner: String): Boolean {
            val calculateHash = BlockUtils.hashByteArray(
                (parentHash + nonce + content + miner).toByteArray()
            )
            return calculateHash.toHexString().contentEquals(blockHash) && BlockUtils.hasLeadingZeroBytes(calculateHash)
        }
        fun isValid(block:Block):Boolean{
            return isValid(block.parentHash, block.nonce, block.content, block.blockHash, block.miner)
        }
    }
}
