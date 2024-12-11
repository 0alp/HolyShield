package com.example.holyshield.interfaces

interface NetworkScanner<T> {
    suspend fun enableScan():List<T>
}