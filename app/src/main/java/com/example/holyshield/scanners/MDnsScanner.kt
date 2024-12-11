package com.example.holyshield.scanners

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.example.holyshield.interfaces.NetworkScanner
import com.example.holyshield.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MDnsScanner(private val context: Context) : NetworkScanner<ScanResult> {

    private val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val tag = "MDNS"


    private val serviceTypes = listOf(
        "_http._tcp",
        "_https._tcp",
        "_printer._tcp",
        "_ipp._tcp",
        "_ftp._tcp",
        "_smb._tcp",
        "_afpovertcp._tcp",
        "_daap._tcp",
        "_airplay._tcp",
        "_raop._tcp",
        "_ssh._tcp",
        "_workstation._tcp",
        "_googlecast._tcp"
    )

    override suspend fun enableScan(): List<ScanResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ScanResult>()

        for (serviceType in serviceTypes) {
            try {
                discoverService(serviceType) { scanResult ->
                    results.add(scanResult)
                }
                delay(2000)
            } catch (e: Exception) {
                Log.e(tag, "Error during discovery: $serviceType, ${e.message}")
            }
        }

        return@withContext results
    }

    private suspend fun discoverService(
        serviceType: String,
        onServiceFound: (ScanResult) -> Unit
    ) = withContext(Dispatchers.IO) {

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                Log.d(tag, "Discovery started for: $serviceType")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.d(tag, "Service found: ${serviceInfo.serviceName} (${serviceInfo.serviceType})")
                resolveService(serviceInfo, onServiceFound)
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(tag, "Discovery stopped for: $serviceType")
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.e(tag, "Service lost: ${serviceInfo.serviceName}")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(tag, "Discovery start failed: $serviceType, Error Code: $errorCode")
                safeStopDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(tag, "Discovery stop failed: $serviceType, Error Code: $errorCode")
                safeStopDiscovery(this)
            }
        }

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        delay(3000)
        safeStopDiscovery(discoveryListener)
    }

    private fun resolveService(serviceInfo: NsdServiceInfo, onServiceFound: (ScanResult) -> Unit) {
        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(tag, "Service resolve failed: ${serviceInfo.serviceName}, Error Code: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(tag, "Service resolved: ${serviceInfo.serviceName}, Host: ${serviceInfo.host}, Port: ${serviceInfo.port}")
                val result = ScanResult(
                    ipAddress = serviceInfo.host,
                    hostname = serviceInfo.serviceName,
                    macAddress = "unknown",
                    openPorts = listOf(serviceInfo.port),
                    source = "mDNS"
                )
                onServiceFound(result)
            }
        }

        nsdManager.resolveService(serviceInfo, resolveListener)
    }

    private fun safeStopDiscovery(listener: NsdManager.DiscoveryListener) {
        try {
            nsdManager.stopServiceDiscovery(listener)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "Listener not registered: ${e.message}")
        }
    }
}
