package dev.lochert.ds.blockchain.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Message(val message:String){
    companion object{
        val ParentHashDoesNotMatch = Message("Parent Hash Does Not Match")
        val OutdatedBlockchain = Message("Your blockchain is outdated")
        val blockAlreadyExists = Message("Block Already is Part of Blockchain")
        val blockAddedSuccessfully = Message(" Added Successfully")

        fun exceptionMessage(e:Exception): Message {
            return Message("${e.javaClass}:\t${e.message}")
        }
    }

    override fun toString(): String {
        return Json.encodeToString(this)
    }

}
