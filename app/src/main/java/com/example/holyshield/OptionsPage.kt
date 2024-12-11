package com.example.holyshield

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.holyshield.screens.NetworkScannerScreen

object OptionsPage: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Options"
            val icon = rememberVectorPainter(Icons.Rounded.PlayArrow)

            return remember {

                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon


                )

            }

        }

    @Composable
    override fun Content() {
        Navigator(NetworkScannerScreen())
    }
}