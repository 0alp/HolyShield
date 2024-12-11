package com.example.holyshield.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator

class Screen2 : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        Column (modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =  Arrangement.Center
        ){
            Text(text = "Screen 2")
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = {
                navigator?.push(Screen3())


            }) {

                Text(text = "Go to Screen 3")


            }

        }
    }
}