package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.block.Block

@Deprecated("Not Used")
interface ControllerFunctions {
    fun allKnownAddress(): Set<Address>
    fun getAllBlocks():List<Block>
    fun getBlock(searchHashHex: String): Block?
    fun getBlockContent(searchHashHex: String):String?

    fun randomSample(prioritiseNew:Boolean = true):List<Address>
    fun addNewAddresses(listOfAddresses: List<Address>)
}