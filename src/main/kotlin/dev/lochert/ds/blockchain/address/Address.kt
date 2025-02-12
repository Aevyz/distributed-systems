package dev.lochert.ds.blockchain.address

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.Instant

@Serializable
data class Address(
    val ip:String,
    val port: Int
){
    init {
        assert(port>0){
            "Port must be greater than 0, is $port"
        }
    }
    @Transient
    var timestamp = Instant.now().epochSecond


    fun updateTime(){
        timestamp = Instant.now().epochSecond
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (ip != other.ip) return false
        if (port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.hashCode()
        result = 31 * result + port
        return result
    }

    override fun toString(): String {
        return "Address(ip='$ip', port=$port, createdTime=$timestamp)"
    }

}
