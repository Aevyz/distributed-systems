package dev.lochert.ds.blockchain.block.balance

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.block.BlockChain

fun BlockChain.getBalances(): List<PublicKeyBalance> {
    val r = mutableMapOf<String, Int>()

    for (block in this.listOfBlocks) {
        if (r.contains(block.miner)) {
            r[block.miner] = r[block.miner]!! + Constants.miningReward
        } else {
            r[block.miner] = Constants.miningReward
        }
    }

    return r.map { PublicKeyBalance(balance = it.value, publicKeyString = it.key) }
}