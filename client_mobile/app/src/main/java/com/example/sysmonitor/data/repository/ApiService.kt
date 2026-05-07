package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.AlertModel
import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.DashboardResponse
import com.example.sysmonitor.data.model.LoginRequest
import com.example.sysmonitor.data.model.LoginResponse
import com.example.sysmonitor.data.model.LogModel
import com.example.sysmonitor.data.model.MetricsModel
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("metrics/servers")
    suspend fun getServerMetrics(@Header("Authorization") token: String): Response<MetricsModel>

    @GET("servers")
    suspend fun getServers(@Header("Authorization") token: String): Response<List<ServerModel>>

    @POST("servers")
    suspend fun createServer(
        @Header("Authorization") token: String,
        @Body request: CreateServerRequest
    ): Response<ServerModel>

    @DELETE("servers/{id}")
    suspend fun deleteServer(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @GET("projects")
    suspend fun getProjects(@Header("Authorization") token: String): Response<List<ProjectModel>>

    @POST("projects")
    suspend fun createProject(
        @Header("Authorization") token: String,
        @Body request: CreateProjectRequest
    ): Response<ProjectModel>

    @DELETE("projects/{id}")
    suspend fun deleteProject(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @GET("alerts")
    suspend fun getAlerts(@Header("Authorization") token: String): Response<List<AlertModel>>

    @GET("logs")
    suspend fun getLogs(@Header("Authorization") token: String): Response<List<LogModel>>

    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<UserResponse>>
}