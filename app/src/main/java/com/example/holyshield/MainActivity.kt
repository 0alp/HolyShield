package com.example.holyshield

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.holyshield.ui.theme.HolyShieldTheme
import com.example.holyshield.utils.PermissionHelper

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permission", "OK")
                println("112- permission +")

            } else {
                Log.d("Permission", "Need required")
                print("112- permission -")

            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!PermissionHelper.isLocationPermissionGranted(this)) {
            PermissionHelper.requestLocationPermission(this)
        }else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {


            HolyShieldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ) {
                    TabNavigator(HomeTab){
                        Scaffold(
                            bottomBar = {
                                NavigationBar {

                                    TabNavigationItem(tab = Tab2)
                                    TabNavigationItem(tab = HomeTab)
                                    TabNavigationItem(tab = ScanTab)
                                }

                            }


                        ) {
                            Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding()))
                            CurrentTab()



                        }

                    }




                }

            }
        }
    }


}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab){
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = {
            tabNavigator.current = tab

        },
        icon = {

            tab.options.icon?.let { painter->
                Icon(
                    painter = painter,
                    contentDescription = tab.options.title
                )



            }



            },
        label = {
            Text(text = tab.options.title)



        })
}


