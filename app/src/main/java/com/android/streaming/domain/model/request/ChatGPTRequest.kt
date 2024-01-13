package com.android.streaming.domain.model.request

import com.squareup.moshi.JsonClass

data class ChatGPTRequest(
    var messages: List<MessageBody>,
    var model: String = "gpt-3.5-turbo",
    var stream: Boolean = true
) {
    companion object {
        fun fromGptBody(messages: List<MessageBody>, model: String): ChatGPTRequest {
            return ChatGPTRequest(messages, model)
        }
    }
}

@JsonClass(generateAdapter = true)
data class MessageBody(
    var role: String,
    var content: String
)