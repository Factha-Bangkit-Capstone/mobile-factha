package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class OcrResponse(
    @field:SerializedName("recognized_text")
    val recognizedText: String? = null
)
