package dev.lochert.ds.blockchain.block

class BlockChain(vararg listOfBlocksArg:Block){
    val listOfBlocks = listOfBlocksArg.toMutableList()


    fun allBlocks():List<Block> = listOfBlocks.toList()
    fun addBlock(block: Block): Block {
        assert(Block.isValid(block))
        if(listOfBlocks.last().blockHash!=block.parentHash){
            println("Warning: Added Hash Does not Match Chain")
        }
        listOfBlocks.add(block)
        return block
    }
    fun addBlock(content:String): Block {
        return addBlock(BlockProposal(listOfBlocks.last().blockHash, content))
    }
    fun addBlock(blockProposal: BlockProposal): Block {
        return addBlock(blockProposal.generateBlock())
    }
    fun searchForBlock(searchHashHex: String): Block = listOfBlocks.first{it.blockHash.contentEquals(searchHashHex)}
}