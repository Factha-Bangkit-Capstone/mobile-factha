package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(

	@field:SerializedName("newsData")
	val newsData: List<NewsDataItem?>? = null
)

data class NewsDataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("newsId")
	val newsId: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("hoaxScore")
	val hoaxScore: Any? = null,

	@field:SerializedName("imageB64")
	val imageB64: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("body")
	val body: String? = null,

	@field:SerializedName("validScore")
	val validScore: Any? = null,

	@field:SerializedName("hoax")
	val hoax: Any? = null,

	@field:SerializedName("tags")
	val tags: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
