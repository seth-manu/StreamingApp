package com.android.streaming.presentation.streaming

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.streaming.R
import com.android.streaming.data.source.remote.ApiService
import com.android.streaming.databinding.ActivityStreamingBinding
import com.android.streaming.domain.model.request.ChatGPTRequest
import com.android.streaming.domain.model.request.MessageBody
import com.android.streaming.domain.model.response.ChatGPTResponse
import com.android.streaming.utils.OpenAiInterceptor
import com.android.streaming.utils.isNetworkAvailable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.ext.android.viewModel
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

private const val TAG = "StreamingActivity"

class StreamingActivity : AppCompatActivity() {

    private lateinit var activityPostsBinding: ActivityStreamingBinding
    private val postViewModel: StreamingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPostsBinding = DataBindingUtil.setContentView(this, R.layout.activity_streaming)

        with(postViewModel) {

            streamData.observe(this@StreamingActivity, Observer {
                Toast.makeText(this@StreamingActivity, it.toString(), LENGTH_LONG).show()
            })

            messageData.observe(this@StreamingActivity, Observer {
                Toast.makeText(this@StreamingActivity, it, LENGTH_LONG).show()
            })
        }

        addClickListener()
    }

    private fun addClickListener() {

        activityPostsBinding.btnSubmit.setOnClickListener {

            val question = activityPostsBinding.editTextQuestion.text.toString().trim()

            if (isNetworkAvailable()) {
                if (!question.equals("", ignoreCase = true)
                ) {
                    postData(question)
//                    postViewModel.getStreamingData(question)
                } else {
                    Toast.makeText(this, "Please enter your question", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
            }
        }

        activityPostsBinding.btnClear.setOnClickListener {
            activityPostsBinding.editTextQuestion.setText("")
            activityPostsBinding.textAnswer.text = ""
        }
    }

    private fun postData(input: String) {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(OpenAiInterceptor())
            .addNetworkInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        val retrofitAPI: ApiService = retrofit.create(ApiService::class.java)

        val tempList = arrayListOf<MessageBody>()
        val messageBody = MessageBody(
            role = "user",
            content = input
        )
        tempList.add(messageBody)

        val modal = ChatGPTRequest(messages = tempList)

        CoroutineScope(Dispatchers.IO).launch {
            val str = StringBuilder()
            getData(retrofitAPI, modal, gson).collectLatest {
                Log.d(TAG, "postData: ${it.choices[0].delta?.content}")
                withContext(Dispatchers.Main) {
                    str.append(it.choices[0].delta?.content)
                    activityPostsBinding.textAnswer.text = str
                }
            }
        }
    }

    private fun getData(retrofitAPI: ApiService, modal: ChatGPTRequest, gson: Gson) = flow {

        val response = retrofitAPI.getStreams(modal).execute()

        if (response.isSuccessful) {
            if (response.isSuccessful) {
                val input = response.body()?.byteStream()?.bufferedReader() ?: throw Exception()
                try {
                    while (currentCoroutineContext().isActive) {
                        val line = input.readLine()
                        if (line != null && line.startsWith("data:")) {
                            try {
                                val groupDeliveryRateInfo = gson.fromJson(
                                    line.substring(5).trim(),
                                    ChatGPTResponse::class.java
                                )
                                emit(groupDeliveryRateInfo)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        delay(100)
                    }
                } catch (e: IOException) {
                    throw Exception(e)
                } finally {
                    input.close()
                }
            } else {
                throw HttpException(response)
            }
        }
    }
}