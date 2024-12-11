package com.example.holyshield.scanners

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class PortScanner {


    suspend fun scan(ipAddress: String, ports: List<Int> = defaultPorts): List<Int> =
        withContext(Dispatchers.IO) {
            ports.filter { port ->
                isPortOpen(ipAddress, port)
            }
        }


    private fun isPortOpen(ipAddress: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ipAddress, port), 200) // 200 ms timeout for ACK
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        /*
            Common TCP ports
        */

        val defaultPorts = listOf(
            20, 21, 22, 23, 25, 53, 80, 110,119,123, 139, 143, 161, 443, 445, 8080, 8443 // socket.connect uses TCP, which is why TCP ports are required
        )
    }
}