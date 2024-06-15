package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class GetSavedNewsResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("news_id")
	val newsId: String? = null
)
