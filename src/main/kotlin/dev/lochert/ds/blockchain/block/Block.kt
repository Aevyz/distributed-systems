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
        return "Block(parentHash='$parentHash', nonce=$nonce, content='$content', blockHash='$blockHash')"
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
