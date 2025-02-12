package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.block.BlockChain

@Deprecated("Not Used")
class ControllerImpl(blockChain: BlockChain = BlockChain(Block.genesisNode)) : ControllerFunctions {
    val addressList = AddressList()
    var blockChain:BlockChain? = null

    override fun allKnownAddress(): Set<Address> {
        return addressList.addressList.toMutableSet()
    }

    override fun getAllBlocks(): List<Block> {
        if(blockChain==null){
            throw IllegalStateException("Has not gotten Blockchain yet")
        }
        return blockChain!!.allBlocks()
    }

    override fun getBlock(searchHashHex: String): Block? {
        return blockChain?.searchForBlock(searchHashHex)
    }

    override fun getBlockContent(searchHashHex: String): String? {
        return blockChain?.searchForBlock(searchHashHex)?.content
    }

    override fun randomSample(prioritiseNew: Boolean): List<Address> {
        return addressList.addressList.shuffled().subList(0, Constants.ADDRESS_PROPAGATION_RANDOM_SAMPLE)
    }

    override fun addNewAddresses(listOfAddresses: List<Address>) {
        listOfAddresses.forEach { addressList.addAddress(it) }
    }
}