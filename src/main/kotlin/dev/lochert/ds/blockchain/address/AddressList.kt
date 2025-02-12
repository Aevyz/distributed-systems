package dev.lochert.ds.blockchain.address

import com.sun.net.httpserver.HttpExchange
import dev.lochert.ds.blockchain.Constants
import kotlinx.serialization.json.Json

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
    fun addAddress(hostname:String, port:Int) {
        addAddress(Address(hostname, port))
    }
    fun addAddress(exchange:HttpExchange) {
        addAddress( Address(exchange.remoteAddress.address.hostAddress, exchange.remoteAddress.port))
    }

    override fun toString(): String {
        return "AddressList(addressList=$addressList)"
    }

}