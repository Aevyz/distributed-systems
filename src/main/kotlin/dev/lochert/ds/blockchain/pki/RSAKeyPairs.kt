package dev.lochert.ds.blockchain.pki

object RSAKeyPairs {
    var listOfKeyPairs: MutableList<RSAKeyPair> = mutableListOf()

    init {
        val threads = mutableListOf<Thread>()
        repeat(26) { counter ->
            val index = 'a' + counter
            val keyPair = RSAKeyPair(index.toString(), (counter + 8000).toUShort())

            synchronized(listOfKeyPairs) {
                listOfKeyPairs += keyPair
            }
        }
    }
}


fun main() {
    // This access will now only happen after init finishes
    println("Key pair count: ${RSAKeyPairs.listOfKeyPairs.size}")
}
