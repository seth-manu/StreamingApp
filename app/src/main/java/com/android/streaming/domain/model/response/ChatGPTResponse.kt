package com.android.streaming.domain.model.response

data class ChatGPTResponse(
    val choices: List<Choice> = arrayListOf(),
    val created: Int = 0,
    val id: String? = "",
    val model: String? = "",
    val `object`: String? = ""
)

data class Choice(
    val delta: Delta? = null
)

class Delta(
    val content: String? = "",
)