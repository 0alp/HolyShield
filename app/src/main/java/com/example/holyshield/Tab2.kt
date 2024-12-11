package com.example.holyshield

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.holyshield.screens.Screen1

object Tab2: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Rounded.Settings)

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
        Navigator(Screen1())
    }
}