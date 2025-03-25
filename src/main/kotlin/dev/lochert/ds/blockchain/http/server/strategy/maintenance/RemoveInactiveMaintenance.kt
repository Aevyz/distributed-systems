package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil

class RemoveInactiveMaintenance : AddressMaintenance {
    override fun maintain(addressList: AddressList) {
        synchronized(addressList) {
            val removeList = addressList.addressList.filter {
                queryNTimes(it)
            }.toList()
            println("[Maintain] [${addressList.ownAddress}] [Inactive] Removing $removeList")
            addressList.addressList.removeAll(removeList.toSet())
        }
    }

    fun queryNTimes(address: Address, tries: UShort = 3U): Boolean {
        repeat(tries.toInt()) {
            try {
                if (HttpUtil.sendGetRequest("http://${address.ip}:${address.port}/").first == 200) {
                    return false
                } else {
                    Thread.sleep(250)
                }
            } catch (e: Exception) {
                Thread.sleep(250)
            }
            println("[Maintain] [Inactive] ($it/$tries) Failed querying $address")

        }
        return true
    }
}