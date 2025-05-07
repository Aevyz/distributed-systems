package dev.lochert.ds.blockchain.block.balance

import dev.lochert.ds.blockchain.pki.RSAKeyPairs
import kotlinx.serialization.Serializable


@Serializable
data class PublicKeyBalance(
    var name: String = "Unknown",
    val balance: Int,
    val publicKeyString: String,
) {
    init {
        name = RSAKeyPairs.getRSAKeyPair(publicKeyString)?.name ?: "Unknown"
    }

    override fun toString(): String {
        return "PublicKeyBalance(name=${name}, balance=$balance, publicKeyString='$publicKeyString')"
    }

}
