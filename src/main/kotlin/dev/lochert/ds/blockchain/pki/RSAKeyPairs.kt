package dev.lochert.ds.blockchain.pki

object RSAKeyPairs {
    var listOfKeyPairs: MutableList<RSAKeyPair> = mutableListOf()

    init {
        listOf("alice", "bob", "carol", "dan", "frank", "george", "mallory").forEach {
            listOfKeyPairs += RSAKeyPair(it)
        }
    }

    val alice = listOfKeyPairs[0]
    val bob = listOfKeyPairs[1]
    val carol = listOfKeyPairs[2]
    val dan = listOfKeyPairs[3]
    val frank = listOfKeyPairs[4]
    val george = listOfKeyPairs[5]
    val mallory = listOfKeyPairs[6]

    fun getRSAKeyPair(publicKeyString: String): RSAKeyPair? {
        return listOfKeyPairs.firstOrNull { it.publicKeyToString() == publicKeyString }
    }
}


fun main() {
    // This access will now only happen after init finishes
    println("Key pair count: ${RSAKeyPairs.listOfKeyPairs.size}")
    RSAKeyPairs.listOfKeyPairs.forEach { println(it) }
}
