package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.block.Block
import dev.lochert.ds.blockchain.block.BlockChain
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.lastInfo
import kotlinx.serialization.json.Json

class ImmediateLongestBlockchainLowestHash : BlockchainMaintenance {
    override fun maintain(addressList: AddressList): BlockChain? {
        val blocks = addressList.addressList.parallelStream().map {
            try {

                val (_, rBody) = HttpUtil.sendGetRequest("http://${it.ip}:${it.port}/block")
                val blocks = Json.decodeFromString<List<Block>>(rBody)
                val blockChain = BlockChain(blocks.first()) // Genesis Block needs to be added like this

                println("[Maintain] [${addressList.ownAddress}] [Blockchain] Success: http://${it.ip}:${it.port}/block returned ${blocks.size} blocks")
                for (block in blocks) {
                    if (block == blocks.first()) { // Skip genesis block
                        continue
                    }
                    blockChain.addBlock(block) // Also Validates if Blocks are correct
                }
                blockChain

            } catch (e: Exception) {
                println("[Maintain] [${addressList.ownAddress}] Failed: http://${it.ip}:${it.port}/block with error ${e.javaClass}")
                null
            }
        }.toList().filterNotNull()

        val blocksSorted =
            blocks.sortedWith(compareByDescending<BlockChain> { it.listOfBlocks.size }.thenBy { it.listOfBlocks.last().blockHash })
        println("[Maintain] [${addressList.ownAddress}] " + blocksSorted.map { it.lastInfo() })
        return blocksSorted.firstOrNull()
    }
}