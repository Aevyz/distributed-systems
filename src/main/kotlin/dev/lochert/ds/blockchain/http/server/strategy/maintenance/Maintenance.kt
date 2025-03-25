package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.block.BlockChain

interface AddressMaintenance {
    // Add new to Address List
    fun maintain(addressList: AddressList)
}

interface BlockchainMaintenance {
    //    Returns a new blockchain
    fun maintain(addressList: AddressList): BlockChain?
}