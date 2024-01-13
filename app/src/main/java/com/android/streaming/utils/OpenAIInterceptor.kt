package com.android.streaming.utils

import android.util.Log
import com.android.streaming.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class OpenAiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.GPT_KEY}")
            .build();

        Log.d("headers", authenticatedRequest.headers["Authorization"]!!);
        return chain.proceed(authenticatedRequest);
    }
}
