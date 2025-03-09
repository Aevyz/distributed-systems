package dev.lochert.ds.blockchain.address

import dev.lochert.ds.blockchain.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AddressList(val ownAddress: Address, val addressList: MutableSet<Address> = mutableSetOf()){
    init{
//        Constants.initialAddressSet.forEach{addAddress(it)}
    }
    fun addressListToJson(): String {
        return Json.encodeToString(addressList)
    }
    fun addAddress(address: Address) {
        if(ownAddress.equals(address)){
            println("Should not add own address")
            return
        }
        val findAddressInAddressList = addressList.firstOrNull { it==address }
        if(findAddressInAddressList==null){
            println("Adding address: $address")
            addressList+=address
        }
        else{
            println("Updating Timestamp of Address: $address")
            findAddressInAddressList.updateTime()
        }
    }
    fun addAddress(hostname:String, port:UShort) {
        addAddress(Address(hostname, port))
    }

    override fun toString(): String {
        return "AddressList(addressList=$addressList)"
    }

}