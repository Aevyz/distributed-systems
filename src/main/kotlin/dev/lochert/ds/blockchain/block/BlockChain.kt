package dev.lochert.ds.blockchain.block

class BlockChain(genesisBlock: Block){
    val listOfBlocks = mutableListOf(genesisBlock)


    fun allBlocks():List<Block> = listOfBlocks.toList()
    fun addBlock(block: Block): Block {
        assert(Block.isValid(block))
        assert(listOfBlocks.last().blockHash==block.parentHash)
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