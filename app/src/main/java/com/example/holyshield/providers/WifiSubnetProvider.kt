package com.example.holyshield.providers

import android.net.wifi.WifiManager
import com.example.holyshield.interfaces.SubnetProvider

class WifiSubnetProvider(private val wifiManager: WifiManager): SubnetProvider {
    override fun getSubnet(): String? {
        val dhcpInfo = wifiManager.dhcpInfo
        val gatewayIp = dhcpInfo.gateway
        return "${gatewayIp and 0XFF}.${gatewayIp shr 8 and 0XFF}.${gatewayIp shr 16 and 0xFF}"
    }
}