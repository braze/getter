package com.example.getter.flip.api

import com.example.getter.flip.data.FlipResult
import retrofit2.http.GET

interface YesNoService {

    @GET("/api")
    suspend fun retrieveFlipper(): FlipResult
}