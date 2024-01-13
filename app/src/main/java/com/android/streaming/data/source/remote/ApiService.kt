package com.android.streaming.data.source.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {

    @Streaming
    @POST("completions")
    fun getStreams(@Body gptBody: Any?): Call<ResponseBody>
}