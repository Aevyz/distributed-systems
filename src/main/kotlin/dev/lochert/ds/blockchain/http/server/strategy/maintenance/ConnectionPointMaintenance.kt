package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy.addressMapToGraph
import dev.lochert.ds.blockchain.http.server.strategy.address.subgraph.SubgraphStrategy.createAddressMap
import kotlinx.serialization.json.Json
import kotlin.math.min

class ConnectionPointMaintenance : AddressMaintenance {
    override fun maintain(addressList: AddressList) {
        val g = addressMapToGraph(
            createAddressMap(addressList.addressList, false),
            listOf(addressList.ownAddress)
        )
        println(g.nodes.map { it.address })

        val subsets = g.findConnectedComponents()

        val connections = addressList.addressList
        val connectCandidates = mutableSetOf<Address>()
        for (subset in subsets) {
            val connectedMembersOfSubset =
                connections.count { subset.map { subsetItem -> subsetItem.address }.contains(it) }
            if (connectedMembersOfSubset < Constants.subgraphConnectionPoints) {
                val connectionsNeeded = min(subset.size, Constants.subgraphConnectionPoints - connectedMembersOfSubset)
                connectCandidates.addAll(
                    subset
                        .sortedBy { it.toString() }
                        .shuffled(Constants.setRandom)
                        .take(connectionsNeeded)
                        .map { it.address })

            }
        }

        println(subsets.map { it.map { it.address.port } })
        println(connectCandidates)
        for (candidateAddress in connectCandidates) {
            try {
                println("[Maintain] [${addressList.ownAddress}] [Subgraph] Connecting to $candidateAddress")
                val (_, _) = HttpUtil.sendPostRequest(
                    "http://${candidateAddress.ip}:${candidateAddress.port}/address",
                    Json.encodeToString(addressList.ownAddress)
                )
                addressList.addAddress(candidateAddress)
            } catch (e: Exception) {
                println("(${addressList.ownAddress}) Encountered an error that prevented the adding of $candidateAddress")
                e.printStackTrace()
            }
        }
    }
}