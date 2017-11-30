package com.gropse.serviceme.socaillogin

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SocialBean(
        /**
         * id : 17
         * full_name : aman mishra
         * email : ashu111111111211111@gmail.com
         * mobile : 992233665588111111111211111
         * device_token : device_token
         * login_type : mail
         * image :
         * status : 0
         * mail_sent : true
         */

        @SerializedName("id") var id: String = "",
        @SerializedName("full_name") var fullName: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("device_token") var deviceToken: String = "",
        @SerializedName("login_type") var loginType: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("status") var status: Int = 0,
        @SerializedName("mail_sent") var mailSent: Boolean = false,
        @SerializedName("password") var password: String = "",
        @SerializedName("uid") var uid: String = ""
) : Serializable