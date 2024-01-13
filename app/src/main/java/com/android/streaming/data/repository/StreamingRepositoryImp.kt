package com.android.streaming.data.repository

import com.android.streaming.data.source.remote.ApiService
import com.android.streaming.domain.repository.StreamingRepository
import okhttp3.ResponseBody
import retrofit2.Call

class StreamingRepositoryImp(private val apiService: ApiService) : StreamingRepository {

    override suspend fun getStreams(body: Any?): Call<ResponseBody> {
        return apiService.getStreams(body)
    }
}