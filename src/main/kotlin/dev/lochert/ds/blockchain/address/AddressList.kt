package dev.lochert.ds.blockchain.address

import dev.lochert.ds.blockchain.Constants
import kotlinx.serialization.json.Json

class AddressList(val addressList: MutableSet<Address> = Constants.initialList.toMutableSet()){
    fun addressListToJson(): String {
        return Json.encodeToString(addressList)
    }
    fun addAddress(address: Address) {
        addressList+=address
    }
    fun addAddress(hostname:String, port:Int) {
        addressList+= Address(hostname, port)
    }
}