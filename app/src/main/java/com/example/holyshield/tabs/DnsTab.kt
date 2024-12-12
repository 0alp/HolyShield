package com.example.holyshield.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.holyshield.screens.DnsResolverScreen

object DnsTab: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "DNS Lookup"
            val icon = rememberVectorPainter(Icons.Rounded.ArrowBack)

            return remember {

                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon


                )

            }

        }

    @Composable
    override fun Content() {
        Navigator(DnsResolverScreen())
    }
}