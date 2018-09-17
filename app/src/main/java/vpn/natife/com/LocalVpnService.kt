package vpn.natife.com

import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.util.Log
import org.jetbrains.anko.doAsync
import java.io.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class LocalVpnService : VpnService() {
    private var tunnel: DatagramChannel? = null
    private var mInterface: ParcelFileDescriptor? = null
    override fun onCreate() {
        super.onCreate()
        Log.d("MYVPN", "Service on create")

        tunnel = DatagramChannel.open()
        val server = InetSocketAddress("31.148.219.165", 4500)
        doAsync {
            tunnel?.connect(server)
            Log.d("MYVPN", "COnnected: ${tunnel?.isConnected}, ${tunnel?.remoteAddress}, socket connected: ${tunnel?.socket()?.isConnected}")
            mInterface = Builder().addAddress("31.148.219.165", 32).addRoute("0.0.0.0", 0).establish()
            val fis = FileInputStream(mInterface?.fileDescriptor)
            val fos = FileOutputStream(mInterface?.fileDescriptor)
            doAsync {
                while (true){
                    try {
                        val out = PrintWriter(fos, true)
//                        Log.d("VPNOUT", "Out: $out")
                    }catch (e: Exception) {

                    }
                }
            }

            doAsync {
                while (true){
                    try {
                        val reader = BufferedReader(InputStreamReader(fis))
                        Log.d("VPNIN", "In: ${reader.readText()}")
                    }catch (e: Exception){

                    }

                }
            }

            val goolge = "https://google.com".toByteArray()
            Log.d("MYVPN", "${mInterface?.fd}, ${mInterface?.statSize}")
            Log.d("MYVPN", "Request ${tunnel?.send(ByteBuffer.wrap(goolge), server)}")
            Log.d("MYVPN", "Responce: ${tunnel?.receive(ByteBuffer.wrap(goolge))}")
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("MYVPN", "on bind")
        return super.onBind(intent)
    }

    override fun onRevoke() {
        super.onRevoke()
        Log.d("MYVPN", "on revoke")
        tunnel?.disconnect()
        mInterface?.close()
        stopSelf()
    }

    class MyRunnable(val fd: FileDescriptor) : Runnable {
        override fun run() {
            val reader = FileReader(fd)
            var strings = if (reader.readLines().isNotEmpty()) reader.readLines() else null
            while (strings != null && strings.isNotEmpty()) {
                strings.forEach {
                    Log.d("MYVPN", "String: $it")
                }
                strings = if (reader.readLines().isNotEmpty()) reader.readLines() else null
            }
        }
    }
}