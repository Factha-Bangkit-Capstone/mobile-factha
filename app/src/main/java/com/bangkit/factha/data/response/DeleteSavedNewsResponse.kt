package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class DeleteSavedNewsResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
