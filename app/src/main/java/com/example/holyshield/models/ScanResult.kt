package com.example.holyshield.models

import java.net.InetAddress

data class ScanResult(
    val ipAddress: InetAddress,
    val macAddress: String? = null,
    val hostname: String? = null,
    val openPorts: List<Int> = emptyList(),
    val source: String = "Unknown"  // From which source found out the result? (ICMP, ARP etc.)



)
