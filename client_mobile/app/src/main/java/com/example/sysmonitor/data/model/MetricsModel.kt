package com.example.sysmonitor.data.model

data class MetricsModel(
    val cpuUsage: Float,
    val ramUsage: Float,
    val diskUsage: Float,
    val runningProcesses: Int,
    val ramUsedGb: Float,
    val ramTotalGb: Float,
    val diskUsedGb: Float,
    val diskTotalGb: Float,
    val timestamp: Long = System.currentTimeMillis()
)