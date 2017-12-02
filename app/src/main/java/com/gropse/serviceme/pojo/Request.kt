package com.gropse.serviceme.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginRequest(
        @SerializedName("email") var email: String = "",
        @SerializedName("password") var password: String = "",
        @SerializedName("device_type") var deviceType: String = "",
        @SerializedName("device_id") var deviceId: String = "",
        @SerializedName("device_token") var deviceToken: String = "",

        @SerializedName("token") var token: String = "",
        @SerializedName("type") var type: String = "",

        @SerializedName("latitude") var latitude: String = "",
        @SerializedName("longitude") var longitude: String = ""
)

data class SignUpRequest(
        //FOR INDIVIDUAL
        @SerializedName("name") var name: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("password") var password: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("services") var services: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("device_type") var deviceType: String = "",
        @SerializedName("device_id") var deviceId: String = "",
        @SerializedName("device_token") var deviceToken: String = "",

        //FOR COMPANY
        @SerializedName("registration_no") var registrationNo: String = "",
        @SerializedName("license_no") var licenseNo: String = "",
        @SerializedName("website") var website: String = "",

        //FOR USER
        @SerializedName("type") var type: String = "",
        @SerializedName("token") var token: String = "",
        @SerializedName("is_email_verified") var isEmailVerified: Int = -1,
        @SerializedName("image") var image: String = "",

        @SerializedName("latitude") var latitude: String = "0",
        @SerializedName("longitude") var longitude: String = "0",
        @SerializedName("city") var city: String = "",

        //FOR GET PROFILE
        @SerializedName("user_id") var userId: String = ""
) : Serializable

data class EditProfileRequest(
        @SerializedName("name") var name: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("website") var website: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("user_id") var userId: String = ""
) : Serializable

data class EditProfileUserRequest(
        @SerializedName("name") var name: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("user_id") var userId: String = ""
) : Serializable

data class OtpRequest(
        //FOR FORGOT PASSWORD, SEND OTP
        @SerializedName("email") var email: String = "",
        @SerializedName("mobile") var mobile: String = "",

        //FOR VERIFY OTP
        @SerializedName("otp") var otp: String = "",
        @SerializedName("security_token") var securityToken: String = "",

        //FOR CHANGE PASSWORD
        @SerializedName("password") var password: String = ""
) : Serializable

data class ChangePassRequest(
        @SerializedName("user_id") var userId: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("old_password") var oldPassword: String = "",
        @SerializedName("new_password") var newPassword: String = ""
) : Serializable

data class CommonRequest(
        @SerializedName("user_id") var userId: String = "",
        @SerializedName("pro_id") var providerId: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("ser_id") var serId: String = "",
        @SerializedName("rate") var rate: Int = 0,
        @SerializedName("status") var status: Int = 0,
        @SerializedName("value") var value: Int = 0,
        @SerializedName("plan_id") var planId: String = "",
        @SerializedName("code") var code: String = "",
        @SerializedName("time") var time: Long = 0L,
        @SerializedName("feedback") var feedback: String = "",
        @SerializedName("latitude") var latitude: String = "",
        @SerializedName("longitude") var longitude: String = "",
        @SerializedName("transaction_id") var transactionId: String = "",
        @SerializedName("device_id") var deviceId: String = ""
) : Serializable

data class AddServiceRequest(
        @SerializedName("ser_name") var serName: String = "",
        @SerializedName("ser_description") var serDescription: String = "",
        @SerializedName("ser_time") var serTime: Long = 0L,
        @SerializedName("ser_cat_id") var serCatId: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("latitude") var latitude: String = "",
        @SerializedName("longitude") var longitude: String = "",
        @SerializedName("user_id") var userId: String = "",
        @SerializedName("ser_type") var serType: String = "",
        @SerializedName("is_scheduled") var isScheduled: String = "",
        @SerializedName("files") var files: String = "",
        @SerializedName("images") var images: ArrayList<String> = ArrayList(),
        @SerializedName("videos") var videos: ArrayList<String> = ArrayList()


) : Serializable

data class SubscriptionRequest(
        @SerializedName("user_id") var userId: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("type") var type: Int = -1,
        @SerializedName("plan_id") var planId: String = "",
        @SerializedName("code") var code: String = "",
        @SerializedName("plan_duration") var planDuration: Long = 0,
        @SerializedName("transaction_type") var transactionType: Int = 0,
        @SerializedName("transaction_id") var transactionId: String = ""
) : Serializable

