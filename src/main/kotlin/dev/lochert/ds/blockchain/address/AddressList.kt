package dev.lochert.ds.blockchain.address

import dev.lochert.ds.blockchain.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AddressList(val addressList: MutableSet<Address> = Constants.initialList.toMutableSet()){
    fun addressListToJson(): String {
        return Json.encodeToString(addressList)
    }
    fun addAddress(address: Address) {

        val addr = addressList.firstOrNull { it==address }
        if(addr==null){
            println("Adding address: $address")
            addressList+address
        }
        else{
            println("Updating Timestamp of Address: $address")
            addr.updateTime()
        }
        addressList+=address
    }
    fun addAddress(hostname:String, port:UShort) {
        addAddress(Address(hostname, port))
    }

    override fun toString(): String {
        return "AddressList(addressList=$addressList)"
    }

}