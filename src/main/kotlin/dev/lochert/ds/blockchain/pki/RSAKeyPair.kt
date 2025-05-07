package dev.lochert.ds.blockchain.pki

import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

@OptIn(ExperimentalStdlibApi::class)
class RSAKeyPair(val name: String, dir: String = "src/main/resources/rsa", keySize: Int = 2048 / 4) {

    val publicKey: PublicKey
    val privateKey: PrivateKey
    private val publicFile: File
    private val privateFile: File

    override fun toString(): String {
        return "${publicKeyToString().substring(100, 106)}($name)"
    }

    fun publicKeyToString() = publicKey.encoded.toHexString()

    init {
        val keyDir = File(dir)
        if (!keyDir.exists()) keyDir.mkdirs()

        publicFile = File(keyDir, "${name}_public.key")
        privateFile = File(keyDir, "${name}_private.key")

        if (publicFile.exists() && privateFile.exists()) {
            publicKey = loadPublicKey(publicFile)
            privateKey = loadPrivateKey(privateFile)
        } else {
            val keyPair = generateKeyPair(keySize)
            publicKey = keyPair.public
            privateKey = keyPair.private
            saveKey(publicFile, publicKey.encoded)
            saveKey(privateFile, privateKey.encoded)
        }
    }

    fun encrypt(data: String): String {
        return encrypt(data.toByteArray())
    }

    fun encrypt(data: ByteArray): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(data)
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptByteString(base64Encrypted: String): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64Encrypted))
        return decryptedBytes
    }

    fun decrypt(base64Encrypted: String): String {
        return String(decryptByteString(base64Encrypted))
    }

    private fun generateKeyPair(keySize: Int): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(keySize)
        return keyGen.generateKeyPair()
    }

    private fun saveKey(file: File, key: ByteArray) {
        file.writeBytes(key)
    }

    private fun loadPublicKey(file: File): PublicKey {
        val spec = X509EncodedKeySpec(file.readBytes())
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    private fun loadPrivateKey(file: File): PrivateKey {
        val spec = PKCS8EncodedKeySpec(file.readBytes())
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }
}

// This code has been generated with the assistance of ChatGPT
// End of generated code
