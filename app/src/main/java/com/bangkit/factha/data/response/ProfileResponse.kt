package com.bangkit.factha.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("userData")
	val userData: List<UserDataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class UserDataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("premium")
	val premium: Int? = null,

	@field:SerializedName("imageB64")
	val imageB64: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("body")
	val body: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
