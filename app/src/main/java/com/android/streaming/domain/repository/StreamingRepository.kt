package com.android.streaming.domain.repository

import com.android.streaming.domain.model.request.ChatGPTRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface StreamingRepository {
    suspend fun getStreams(body: Any?): Call<ResponseBody>
}