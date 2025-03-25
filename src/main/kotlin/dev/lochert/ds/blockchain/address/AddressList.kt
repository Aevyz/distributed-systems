package dev.lochert.ds.blockchain.address

import dev.lochert.ds.blockchain.Constants
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Serializable
class AddressList(val ownAddress: Address, val addressList: MutableSet<Address> = mutableSetOf()){
    constructor(ownAddress: Address, file: File) : this(
        ownAddress,
        FileReader(file).use { Json.decodeFromString<MutableSet<Address>>(it.readText()) }
    )

    init {
        if (Constants.readAddressBook) {
            try {
                println("Adding addresses from backup file")
                addressList += FileReader(expectedPath()).use { Json.decodeFromString<MutableSet<Address>>(it.readText()) }
            } catch (e: Exception) {
                println("Could not read past address book!")
                e.printStackTrace()
            }
        }
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
        if (findAddressInAddressList == null) {
            println("Adding address: $address")
            synchronized(this) {
                addressList += address
            }
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


    private fun expectedPath() = "/tmp/address/${ownAddress.ip}.${ownAddress.port}.addressbackup"

    fun exportToFile() {
        File("/tmp/address").mkdirs()
        val path = File(expectedPath())
        if (path.exists()) {
            path.delete()
        }
        path.createNewFile()
        FileWriter(path).use { it.write(addressListToJson()) }
    }

}