package dev.lochert.ds.blockchain.block.balance

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.block.BlockChain

fun BlockChain.getBalances(upToIndex: Int = this.listOfBlocks.size): List<BalanceEntry> {
    val r = mutableMapOf<String, Double>()
    var c = 0
    for (block in this.listOfBlocks) {
        if (r.contains(block.miner)) {
            r[block.miner] = r[block.miner]!! + Constants.miningReward
        } else {
            r[block.miner] = Constants.miningReward
        }
        for (transaction in block.transactions) {
            if (transaction.sender !in r) {
                r[transaction.sender] = 0.0
            }
            if (transaction.receiver !in r) {
                r[transaction.receiver] = 0.0
            }
            r[transaction.sender] = r[transaction.sender]!! - transaction.amount - transaction.transactionMinerReward
            r[transaction.receiver] = r[transaction.receiver]!! + transaction.amount
            r[block.miner] = r[block.miner]!! + transaction.transactionMinerReward
        }
        if (c++ == upToIndex) {
            break
        }
    }

    return r.map { BalanceEntry(balance = it.value, publicKeyString = it.key) }
}