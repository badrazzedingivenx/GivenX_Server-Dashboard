package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.MetricsModel

interface MetricsRepository {
    suspend fun getMetrics(): Result<MetricsModel>
    suspend fun refreshMetrics(): Result<MetricsModel>
}