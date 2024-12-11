package com.example.holyshield.scanners

import com.example.holyshield.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class IntegratedScanner(
    private val icmpScanner: IcmpScanner,
    private val arpScanner: ArpScanner,
    private val portScanner: PortScanner

) {


    suspend fun integratedScan(): List<ScanResult> = withContext(Dispatchers.Default) {
        // Execute ARP, ICMP etc. scans in parallel
        val icmpResultsDeferred = async { icmpScanner.enableScan() }
        val arpResultsDeferred = async { arpScanner.enableScan() }



        val icmpResults = icmpResultsDeferred.await()
        val arpResults = arpResultsDeferred.await()



        val combinedResults = mergeScanResults(icmpResults, arpResults)


        combinedResults.map { result ->
            async {
                val openPorts = portScanner.scan(result.ipAddress.hostAddress)
                result.copy(openPorts = openPorts)
            }
        }.awaitAll() // wait for port scans.
    }


    private fun mergeScanResults(
        icmpResults: List<ScanResult>,
        arpResults: List<ScanResult>,

    ): List<ScanResult> {
        val mergedResults = mutableMapOf<String, ScanResult>()


        icmpResults.forEach { result ->
            mergedResults[result.ipAddress.hostAddress] = result
        }


        arpResults.forEach { arpResult ->
            val ipAddress = arpResult.ipAddress.hostAddress
            val existingResult = mergedResults[ipAddress]

            if (existingResult != null) {
                mergedResults[ipAddress] = existingResult.copy(
                    macAddress = arpResult.macAddress ?: existingResult.macAddress,
                    hostname = arpResult.hostname ?: existingResult.hostname
                )
            } else {
                mergedResults[ipAddress] = arpResult
            }
        }




        return mergedResults.values.toList()
    }



}