package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.LoginRequest
import com.example.sysmonitor.data.model.LoginResponse
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // ── AUTH ──────────────────────────────
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // ── SERVERS ───────────────────────────
    @GET("api/servers")
    suspend fun getServers(): Response<List<ServerModel>>

    // ── PROJECTS ──────────────────────────
    @GET("api/projects")
    suspend fun getProjects(): Response<List<ProjectModel>>

    @POST("api/projects")
    suspend fun createProject(
        @Body request: CreateProjectRequest
    ): Response<ProjectModel>
}