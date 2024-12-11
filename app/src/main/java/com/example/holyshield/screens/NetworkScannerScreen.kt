package com.example.holyshield.screens

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.holyshield.providers.WifiSubnetProvider
import com.example.holyshield.scanners.ArpScanner
import com.example.holyshield.scanners.IcmpScanner
import com.example.holyshield.scanners.IntegratedScanner
import com.example.holyshield.scanners.PortScanner
import kotlinx.coroutines.launch

class NetworkScannerScreen : Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val wifiManager = remember { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
        val subnetProvider = remember { WifiSubnetProvider(wifiManager) }
        val icmpScanner = remember { IcmpScanner(subnetProvider) }
        val arpScanner = remember { ArpScanner() }
        val portScanner = PortScanner()
        val integratedScanner = remember { IntegratedScanner(icmpScanner, arpScanner, portScanner) }
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "NETWORK SCANNER")
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = {
                coroutineScope.launch {
                    println("112- Scanning has been started")


                    val results = integratedScanner.integratedScan()


                    results.forEach { result ->
                        println("112- Source: ${result.source}, Device: IP=${result.ipAddress.hostAddress}, MAC=${result.macAddress ?: "Bilinmiyor"}, Hostname=${result.hostname ?: "Unknown"}, Open Ports=${result.openPorts.ifEmpty { "unknown" } }")
                    }

                    println("112- Scaning has been complated")
                }
            }) {
                Text(text = "START SCAN")
            }
        }
    }
}