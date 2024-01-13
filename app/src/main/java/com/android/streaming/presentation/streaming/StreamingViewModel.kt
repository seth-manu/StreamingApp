package com.android.streaming.presentation.streaming

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streaming.domain.model.ApiError
import com.android.streaming.domain.model.request.ChatGPTRequest
import com.android.streaming.domain.model.request.MessageBody
import com.android.streaming.domain.usecase.StreamingUseCase
import com.android.streaming.domain.usecase.base.UseCaseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StreamingViewModel(private val getPostsUseCase: StreamingUseCase) : ViewModel() {

    val streamData = MutableLiveData<Any>()
    val showProgressbar = MutableLiveData<Boolean>()
    val messageData = MutableLiveData<String>()

    fun getStreamingData(question: String) {

        CoroutineScope(Dispatchers.IO).launch {

            val msgList = arrayListOf<MessageBody>()
            msgList.add(
                MessageBody(
                    role = "user",
                    content = question
                )
            )

            showProgressbar.postValue(true)
            getPostsUseCase.invoke(
                viewModelScope, msgList,
                object : UseCaseResponse<ChatGPTRequest> {

                    override fun onSuccess(result: Any) {
                        Log.i(TAG, "result: $result")
                        streamData.postValue(result)
                        showProgressbar.postValue(false)
                    }

                    override fun onError(apiError: ApiError?) {
                        messageData.postValue(apiError?.getErrorMessage())
                        showProgressbar.postValue((false))
                    }
                },
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        private val TAG = StreamingViewModel::class.java.name
    }

}