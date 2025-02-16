package dev.lochert.ds.blockchain.http

import kotlinx.serialization.Serializable

@Serializable
data class Message(val message:String){
    companion object{
        val ParentHashDoesNotMatch = Message("Parent Hash Does Not Match")
        val OutdatedBlockchain = Message("Your blockchain is outdated")
        val blockAlreadyExists = Message("Block Already is Part of Blockchain")
        val blockAddedSuccessfully = Message("Block Added Successfully")
    }
}
