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

    // Looks for the existence of the provided block in the list
    fun doesBlockExist(searchHashHex: String): Boolean {
        listOfBlocks.forEach{
            if (it.blockHash == searchHashHex) {
                return true
            }
        }
        return false
    }
    fun searchForBlock(searchHashHex: String): Block = listOfBlocks.first{it.blockHash.contentEquals(searchHashHex)}

    // Returns the list of blocks starting from the block with the provided hash
    fun searchForBlocksFrom(searchHashHex: String): MutableList<Block> {

        val startOfSubList = listOfBlocks.indexOf(this.searchForBlock(searchHashHex))
        val endOfSubList = listOfBlocks.size
        return listOfBlocks.subList(startOfSubList, endOfSubList)
    }
}