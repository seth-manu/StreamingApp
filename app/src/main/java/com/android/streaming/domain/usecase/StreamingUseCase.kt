package com.android.streaming.domain.usecase

import com.android.streaming.domain.model.request.ChatGPTRequest
import com.android.streaming.domain.repository.StreamingRepository
import com.android.streaming.domain.usecase.base.UseCase
import okhttp3.ResponseBody
import retrofit2.Call

class StreamingUseCase(
    private val postsRepository: StreamingRepository
) : UseCase<ChatGPTRequest, Any?>() {

    override suspend fun run(params: Any?): Call<ResponseBody> {
        return postsRepository.getStreams(params)
    }
}