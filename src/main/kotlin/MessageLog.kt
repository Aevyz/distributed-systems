import java.net.InetAddress
import java.util.Date

data class MessageLog(
    val timeReceived: Date,
    val source: NodeAddress,
    val destination: NodeAddress,
  val messageSize: UInt)
