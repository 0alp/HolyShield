package com.example.holyshield.screens

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.holyshield.models.ScanResult
import com.example.holyshield.providers.WifiSubnetProvider
import com.example.holyshield.scanners.ArpScanner
import com.example.holyshield.scanners.IcmpScanner
import com.example.holyshield.scanners.IntegratedScanner
import com.example.holyshield.scanners.MDnsScanner
import com.example.holyshield.scanners.PortScanner
import kotlinx.coroutines.launch

class NetworkScannerScreen : Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val wifiManager = remember { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
        val subnetProvider = remember { WifiSubnetProvider(wifiManager) }
        val integratedScanner = IntegratedScanner(
            scanners = listOf(IcmpScanner(subnetProvider), ArpScanner(), MDnsScanner(context)),
            portScanner = PortScanner()
        )
        val coroutineScope = rememberCoroutineScope()


        var scanResults by rememberSaveable { mutableStateOf<List<ScanResult>>(emptyList()) }
        var isLoading by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "NETWORK SCANNER", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        scanResults = emptyList()


                        val results = integratedScanner.integratedScan()
                        scanResults = results

                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Scanning..." else "START SCAN")
            }

            Spacer(modifier = Modifier.height(20.dp))


            if (isLoading) {
                CircularProgressIndicator()
            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(scanResults) { result ->
                        ResultRow(result)
                    }
                }
            }
        }
    }

    @Composable
    private fun ResultRow(result: ScanResult) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Source: ${result.source}")
            Text(text = "IP: ${result.ipAddress.hostAddress}")
            Text(text = "MAC: ${result.macAddress ?: "unknown"}")
            Text(text = "Hostname: ${result.hostname ?: "Unknown"}")
            Text(text = "Open Ports: ${result.openPorts.ifEmpty { "unknown" }}")
        }
    }
}
