package com.android.streaming.domain.usecase.base

import com.android.streaming.domain.model.ApiError

interface UseCaseResponse<Type> {

    fun onSuccess(result: Any)

    fun onError(apiError: ApiError?)
}

