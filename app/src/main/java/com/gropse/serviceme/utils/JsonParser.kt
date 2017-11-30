package com.gropse.serviceme.utils

import android.content.Context
import com.google.gson.Gson
import com.gropse.serviceme.pojo.ProfileResult
import org.json.JSONArray

import org.json.JSONObject


class JsonParser(context: Context?, response: String, isToNotifyUser: Boolean = true) {
    private var json: JSONObject? = null
    private val TAG = JsonParser::class.java.name
    var isSuccess: Boolean = false
    var errorCode: Int = 0
    var message = ""
    var result = ""
    var jsonObject: JSONObject? = null
    var jsonArray: JSONArray? = null

    init {
        try {
            json = JSONObject(response)
            isSuccess = json?.optBoolean("isSuccess") ?: false
            errorCode = json?.optInt("errorCode") ?: 0
            message = json?.optString("message") ?: ""
            result = Gson().toJson(json?.opt("Result"))
//            if (json?.opt("Result") is JSONObject) {
//                jsonObject = json?.optJSONObject("Result")
//
//            } else {
//                jsonArray = json?.optJSONArray("Result")
//            }
            if (isToNotifyUser) {
                context?.toast(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    val profileResult: ProfileResult
        get() = Gson().fromJson(result, ProfileResult::class.java)

    val token: String
        get() = JSONObject(result).optString("security_token")
}
