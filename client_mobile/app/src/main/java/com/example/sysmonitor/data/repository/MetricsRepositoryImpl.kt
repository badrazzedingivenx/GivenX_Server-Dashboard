package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.MetricsModel
import kotlinx.coroutines.delay

class MetricsRepositoryImpl : MetricsRepository {

    override suspend fun getMetrics(): Result<MetricsModel> {
        return try {
            // Simulate network delay
            delay(1000)

            // Fake data — replace with real API later
            val fakeData = MetricsModel(
                cpuUsage      = 42.5f,
                ramUsage      = 68.0f,
                diskUsage     = 55.3f,
                runningProcesses = 124,
                ramUsedGb     = 10.9f,
                ramTotalGb    = 16.0f,
                diskUsedGb    = 276.5f,
                diskTotalGb   = 500.0f
            )

            Result.success(fakeData)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshMetrics(): Result<MetricsModel> {
        return getMetrics()
    }
}