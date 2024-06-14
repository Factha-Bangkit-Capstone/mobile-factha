package com.bangkit.factha.data.response

data class AddNewsRequest(
    val userId: String,
    val title: String,
    val tags: String,
    val body: String,
    val image: String
)
