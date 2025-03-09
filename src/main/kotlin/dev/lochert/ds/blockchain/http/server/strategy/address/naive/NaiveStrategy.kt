package dev.lochert.ds.blockchain.http.server.strategy.address.naive

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address
import dev.lochert.ds.blockchain.address.AddressList
import dev.lochert.ds.blockchain.http.HttpUtil
import kotlinx.serialization.json.Json

object NaiveStrategy {
    /**
     * This strategy involves asking all addresses in the initial list for their addresses
     * Adds itself to each list
     */
    fun queryAddressesNaively(thisAddress: Address) : AddressList {
        println("Querying for Addresses")
        val addressList = AddressList(thisAddress)
        Constants.initialAddressSet.forEach { addressList.addAddress(it) }

        Constants.initialAddressSet.parallelStream().forEach { address ->
            try{
                val (_, rBody) = HttpUtil.sendPostRequest("http://${address.ip}:${address.port}/address", Json.encodeToString(thisAddress))
                val receivedAddressList = Json.decodeFromString<List<Address>>(rBody)

                synchronized(addressList){
                    receivedAddressList.forEach { addressList.addAddress(it) }
                }

            }catch (e:Exception){
                println("Failed to connect to http://${address.ip}:${address.port}/address")
            }
        }
        return addressList
    }
}