import java.net.InetAddress
import java.net.InetSocketAddress

data class NodeAddress(val inetAddress: InetAddress, val portUShort: UShort){
    val port = portUShort.toInt()

    val inetSocketAddress = InetSocketAddress(inetAddress, port)
}
