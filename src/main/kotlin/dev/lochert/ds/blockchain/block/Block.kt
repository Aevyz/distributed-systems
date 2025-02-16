package dev.lochert.ds.blockchain.block

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
        blockHash = tHash!!.toHexString()
    }
    constructor(parentHash:String, nonce:Int, content:String, blockHash:String){
        assert(parentHash.isNotBlank()){"Parent Hash cannot be empty"}
        assert(isValid(parentHash, nonce, content, blockHash)){"Block is not valid"}
        this.parentHash = parentHash
        this.nonce=nonce
        this.content=content
        this.blockHash=blockHash
    }
    val parentHash: String
    val nonce:Int
    val content:String
    val blockHash:String
    fun isGenesis() = parentHash.isEmpty()

    override fun toString(): String {
        return "Block(nonce=$nonce, content='$content', parentHash='$parentHash', blockHash='$blockHash')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (nonce != other.nonce) return false
        if (parentHash != other.parentHash) return false
        if (content != other.content) return false
        if (blockHash != other.blockHash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce
        result = 31 * result + parentHash.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + blockHash.hashCode()
        return result
    }


    companion object{
        val genesisNode = Block()
        fun isValid(parentHash:String, nonce:Int, content:String, blockHash:String):Boolean{
            val calculateHash = BlockUtils.hashByteArray(
                (parentHash+nonce+content).toByteArray()
            )
            return calculateHash.toHexString().contentEquals(blockHash) && BlockUtils.hasLeadingZeroBytes(calculateHash)
        }
        fun isValid(block:Block):Boolean{
            return isValid(block.parentHash, block.nonce, block.content, block.blockHash)
        }
    }
}
