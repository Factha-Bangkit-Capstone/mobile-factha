package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class SavedNewsResponse(

	@field:SerializedName("data")
	val data: List<SavedDataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class SavedDataItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("news_id")
	val newsId: String? = null
)
