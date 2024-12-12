package com.example.holyshield.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()


        var ipInfo by remember { mutableStateOf<Map<String, String>?>(null) }
        var isLoading by remember { mutableStateOf(true) }


        LaunchedEffect(Unit) {
            coroutineScope.launch {
                ipInfo = fetchIpInfo()
                isLoading = false
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                ipInfo != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "IP Information",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoTable(data = ipInfo!!)
                    }
                }
                else -> Text(
                    text = "Failed to fetch IP info",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    @Composable
    private fun InfoTable(data: Map<String, String>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            data.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$key:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = value,
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            maxLines = 1
                        )
                    }
                }
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }


    private suspend fun fetchIpInfo(): Map<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://ipinfo.io/json")
                val json = JSONObject(url.readText())
                json.keys().asSequence().associateWith { key -> json.getString(key) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
