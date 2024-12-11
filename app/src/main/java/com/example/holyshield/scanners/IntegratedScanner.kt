package com.example.holyshield.scanners

import com.example.holyshield.interfaces.NetworkScanner
import com.example.holyshield.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class IntegratedScanner(
    private val scanners: List<NetworkScanner<ScanResult>>,
    private val portScanner: PortScanner
) {


    suspend fun integratedScan(): List<ScanResult> = withContext(Dispatchers.Default) {

        val scanResultsDeferred = scanners.map { scanner ->
            async { scanner.enableScan() }
        }


        val scanResults = scanResultsDeferred.awaitAll().flatten()
        val combinedResults = mergeScanResults(scanResults)


        combinedResults.map { result ->
            async {
                val openPorts = portScanner.scan(result.ipAddress.hostAddress)
                result.copy(openPorts = openPorts)
            }
        }.awaitAll()
    }


    private fun mergeScanResults(results: List<ScanResult>): List<ScanResult> {
        val mergedResults = mutableMapOf<String, ScanResult>()

        results.forEach { result ->
            val ipAddress = result.ipAddress.hostAddress
            val existingResult = mergedResults[ipAddress]


            if (existingResult != null) {
                mergedResults[ipAddress] = existingResult.copy(
                    macAddress = result.macAddress ?: existingResult.macAddress,
                    hostname = result.hostname ?: existingResult.hostname
                )
            } else {
                mergedResults[ipAddress] = result
            }
        }

        return mergedResults.values.toList()
    }
}
