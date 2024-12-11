package com.example.holyshield.scanners

import android.util.Log
import com.example.holyshield.interfaces.NetworkScanner
import com.example.holyshield.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.InetAddress

class ArpScanner : NetworkScanner<ScanResult> {

    companion object {
        private const val TAG = "ArpScanner"
    }

    override suspend fun enableScan(): List<ScanResult> {
        return getCombinedArpEntries()
    }

    // Combines ip neigh` and `/proc/net/arp` sources
    private suspend fun getCombinedArpEntries(): List<ScanResult> = withContext(Dispatchers.Default) {
        val arpSources = listOf(
            async { getArpTableFromFile() },
            async { getArpTableFromIpCommand() }
        )

        val results = arpSources.awaitAll().flatten()
        results.distinctBy { it.ipAddress.hostAddress }
    }

    // Reads /proc/net/arp file.(If the device isn't rooted, The function probably doesn't work.)
    private suspend fun getArpTableFromFile(): List<ScanResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ScanResult>()
        try {
            val file = File("/proc/net/arp")
            if (!file.exists() || !file.canRead()) {
                Log.e(TAG, "The /proc/net/arp file cannot be accessed.")
                return@withContext results
            }

            val reader = BufferedReader(FileReader(file))
            reader.useLines { lines ->
                lines.drop(1) // skip the title line
                    .mapNotNull { parseArpLine(it) }
                    .forEach { results.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An error occured while reading the ARP file: ${e.message}")
        }
        results
    }

    // ip neigh
    private suspend fun getArpTableFromIpCommand(): List<ScanResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ScanResult>()
        try {
            val process = Runtime.getRuntime().exec("ip neigh")
            val reader = BufferedReader(process.inputStream.reader())
            reader.useLines { lines ->
                lines.mapNotNull { parseIpNeighLine(it) }
                    .forEach { results.add(it) }
            }
            println(reader.toString())
        } catch (e: Exception) {
            Log.e(TAG, "An error occured while executing the `ip neigh' command: ${e.message}")
        }
        results
    }


    private fun parseArpLine(line: String): ScanResult? {
        return try {
            val parts = line.split("\\s+".toRegex())
            if (parts.size < 6 || parts[3] == "00:00:00:00:00:00") return null
            val ip = parts[0]
            val mac = parts[3]
            val inetAddress = InetAddress.getByName(ip)
            val resolvedHostname = inetAddress.canonicalHostName
            val isHostnameResolved = resolvedHostname != inetAddress.hostAddress

            ScanResult(
                ipAddress = InetAddress.getByName(ip),
                macAddress = mac,
                hostname =  if(isHostnameResolved) resolvedHostname else "unknown",
                source = "ARP"
            )
        } catch (e: Exception) {
            Log.e(TAG, "The ARP line couldn't be parsed: $line, Error: ${e.message}")
            null
        }
    }


    private fun parseIpNeighLine(line: String): ScanResult? {
        return try {
            val parts = line.split("\\s+".toRegex())
            if (parts.size < 5 || parts[4] == "00:00:00:00:00:00") return null
            val ip = parts[0]
            val mac = parts[4]
            val inetAddress = InetAddress.getByName(ip)
            val resolvedHostname = inetAddress.canonicalHostName
            val isHostnameResolved = resolvedHostname != inetAddress.hostAddress
            ScanResult(
                ipAddress = InetAddress.getByName(ip),
                macAddress = mac,
                hostname =  if(isHostnameResolved) resolvedHostname else "unknown",
                source = "ARP"
            )
        } catch (e: Exception) {
            Log.e(TAG, "The `ip neigh` line couldn't be parsed: $line, Error: ${e.message}")
            null
        }
    }
}