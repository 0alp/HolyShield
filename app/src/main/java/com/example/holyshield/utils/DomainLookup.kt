package com.example.holyshield.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

object DomainLookup {

     suspend fun resolveDomainToIp(domain: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inetAddress = InetAddress.getByName(domain)
                inetAddress.hostAddress
            } catch (e: Exception) {
                e.printStackTrace()
                "Resolution failed"
            }
        }
    }

}