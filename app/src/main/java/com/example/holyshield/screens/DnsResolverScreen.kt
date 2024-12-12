package com.example.holyshield.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.holyshield.utils.DomainLookup
import kotlinx.coroutines.launch

class DnsResolverScreen : Screen {
    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()

        var domain by remember { mutableStateOf("") }
        var ipAddress by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Domain to IP Resolver")
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = domain,
                onValueChange = { domain = it },
                label = { Text("Enter domain") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        ipAddress = DomainLookup.resolveDomainToIp(domain)
                        isLoading = false
                    }
                },
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Resolving..." else "Resolve")
            }
            Spacer(modifier = Modifier.height(16.dp))


            ipAddress?.let {
                Text(text = "IP Address: $it")
            }
        }
    }



}
