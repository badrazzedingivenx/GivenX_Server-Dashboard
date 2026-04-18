package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.MetricsModel
import kotlinx.coroutines.delay

class MetricsRepositoryImpl : MetricsRepository {

    override suspend fun getMetrics(): Result<MetricsModel> {
        delay(800)
        // ✅ MOCK — replace with api.getMetrics() when backend is ready
        return Result.success(
            MetricsModel(
                cpuUsage         = 42.5f,
                ramUsage         = 68.0f,
                diskUsage        = 55.3f,
                runningProcesses = 124,
                ramUsedGb        = 10.9f,
                ramTotalGb       = 16.0f,
                diskUsedGb       = 276.5f,
                diskTotalGb      = 500.0f
            )
        )
    }

    override suspend fun refreshMetrics(): Result<MetricsModel> = getMetrics()
}