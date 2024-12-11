package com.example.holyshield.scanners

import android.util.Log
import com.example.holyshield.interfaces.NetworkScanner
import com.example.holyshield.interfaces.SubnetProvider
import com.example.holyshield.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

class IcmpScanner(private val subnetProvider: SubnetProvider) : NetworkScanner<ScanResult> {

    override suspend fun enableScan(): List<ScanResult> {
        val subnet = subnetProvider.getSubnet()
        if (subnet == null) {
            Log.e("ICMP", "The subnet has not been found.")
            return emptyList()
        }

        val results = mutableListOf<ScanResult>()


        withContext(Dispatchers.IO) {
            for (i in 1..254) {
                val host = "$subnet.$i"
                try {
                    val inetAddress = InetAddress.getByName(host)

                    val resolvedHostname = inetAddress.canonicalHostName
                    val isHostnameResolved = resolvedHostname != inetAddress.hostAddress
                    if (inetAddress.isReachable(100)) { //wait 100 seconds for response

                        results.add(
                            ScanResult(
                                ipAddress = inetAddress,
                                source = "ICMP",
                                hostname =  if(isHostnameResolved) resolvedHostname else "unknown"
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ICMP", "An error has occured: ${e.message}")
                }
            }
        }

        return results
    }



}
