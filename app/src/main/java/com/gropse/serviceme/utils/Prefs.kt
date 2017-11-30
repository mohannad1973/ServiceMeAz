package com.gropse.serviceme.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Prefs internal constructor(mContext: Context?) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)

    companion object {

        val TAG = Prefs::class.java.simpleName
        private val PREFS_LOGIN_KEY = "is_user_login_to_classified"
        private val PREFS_DEVICE_TOKEN = "PREFS_DEVICE_TOKEN"
        private val PREFS_DEVICE_ID = "DEVICE_ID"
        private val PREFS_SECURITY_TOKEN = "SECURITY_TOKEN"
        private val PREFS_USER_ID = "PREFS_USER_ID"
        private val PREFS_USER_TYPE = "PREFS_USER_TYPE"
        private val PREFS_NAME = "NAME"
        private val PREFS_EMAIL = "EMAIL"
        private val PREFS_IMAGE = "IMAGE"
        private val PREFS_MOBILE = "MOBILE"
        private val PREFS_LOGIN_TYPE = "LOGIN_TYPE"
        private val PREFS_LOCALE = "LOCALE"
        private val PREFS_LATITUDE = "LATITUDE"
        private val PREFS_LONGITUDE = "LONGITUDE"
        private val PREFS_LOCATION = "LOCATION"
        private val PREFS_COUNTRY_CODE = "COUNTRY_CODE"
        private val PREFS_COUNTRY_NAME = "COUNTRY_NAME"
        private val PREFS_IMAGE_1 = "IMAGE_1"
        private val PREFS_IMAGE_2 = "IMAGE_2"
        private val PREFS_IMAGE_3 = "IMAGE_3"
        private val PREFS_VIDEO_1 = "VIDEO_1"
        private val PREFS_VIDEO_2 = "VIDEO_2"
        private val PREFS_VIDEO_3 = "VIDEO_3"
        private val PREFS_IS_NOTIFICATION_ENABLE = "IS_NOTIFICATION_ENABLE"
        private val PREFS_IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"
        private val ref: Prefs? = null
    }

    var login: Boolean
        get() = sharedPreferences.getBoolean(PREFS_LOGIN_KEY, false)
        set(isLogin) = sharedPreferences.edit().putBoolean(PREFS_LOGIN_KEY, isLogin).apply()

//    fun setLogin() {
//        sharedPreferences.edit().putBoolean(PREFS_LOGIN_KEY, true).apply()
//    }

    var userType: Int
        get() = sharedPreferences.getInt(PREFS_USER_TYPE, -2)
        set(user) = sharedPreferences.edit().putInt(PREFS_USER_TYPE, user).apply()

    var userId: String
        get() = sharedPreferences.getString(PREFS_USER_ID, "")
        set(userId) = sharedPreferences.edit().putString(PREFS_USER_ID, userId).apply()

    var deviceToken: String
        get() = sharedPreferences.getString(PREFS_DEVICE_TOKEN, "")
        set(deviceToken) = sharedPreferences.edit().putString(PREFS_DEVICE_TOKEN, deviceToken).apply()

    var deviceId: String
        get() = sharedPreferences.getString(PREFS_DEVICE_ID, "")
        set(deviceId) = sharedPreferences.edit().putString(PREFS_DEVICE_ID, deviceId).apply()

    var securityToken: String
        get() = sharedPreferences.getString(PREFS_SECURITY_TOKEN, "")
        set(securityToken) = sharedPreferences.edit().putString(PREFS_SECURITY_TOKEN, securityToken).apply()

    var locale: String
        get() = sharedPreferences.getString(PREFS_LOCALE, "")
        set(locale) = sharedPreferences.edit().putString(PREFS_LOCALE, locale).apply()

    var latitude: String
        get() = sharedPreferences.getString(PREFS_LATITUDE, "")
        set(latitude) = sharedPreferences.edit().putString(PREFS_LATITUDE, latitude).apply()

    var longitude: String
        get() = sharedPreferences.getString(PREFS_LONGITUDE, "")
        set(longitude) = sharedPreferences.edit().putString(PREFS_LONGITUDE, longitude).apply()

    var location: String
        get() = sharedPreferences.getString(PREFS_LOCATION, "")
        set(location) = sharedPreferences.edit().putString(PREFS_LOCATION, location).apply()

    var countryCode: String
        get() = sharedPreferences.getString(PREFS_COUNTRY_CODE, "")
        set(countryCode) = sharedPreferences.edit().putString(PREFS_COUNTRY_CODE, countryCode).apply()

    var countryName: String
        get() = sharedPreferences.getString(PREFS_COUNTRY_NAME, "")
        set(countryName) = sharedPreferences.edit().putString(PREFS_COUNTRY_NAME, countryName).apply()

    var name: String
        get() = sharedPreferences.getString(PREFS_NAME, "")
        set(name) {
            if (name.isNotBlank())
                sharedPreferences.edit().putString(PREFS_NAME, name).apply()
        }

    var email: String
        get() = sharedPreferences.getString(PREFS_EMAIL, "")
        set(email) {
            if (email.isNotBlank())
                sharedPreferences.edit().putString(PREFS_EMAIL, email).apply()
        }

    var image: String
        get() = sharedPreferences.getString(PREFS_IMAGE, "")
        set(image) {
            if (image.isNotBlank())
                sharedPreferences.edit().putString(PREFS_IMAGE, image).apply()
        }

    var mobile: String
        get() = sharedPreferences.getString(PREFS_MOBILE, "")
        set(mobile) {
            if (mobile.isNotBlank())
                sharedPreferences.edit().putString(PREFS_MOBILE, mobile).apply()
        }
    var loginType: String
        get() = sharedPreferences.getString(PREFS_LOGIN_TYPE, "")
        set(loginType) {
            if (loginType.isNotBlank())
                sharedPreferences.edit().putString(PREFS_LOGIN_TYPE, loginType).apply()
        }

    var isNotificationEnable: Boolean
        get() = sharedPreferences.getBoolean(PREFS_IS_NOTIFICATION_ENABLE, false)
        set(isNotificationEnable) = sharedPreferences.edit().putBoolean(PREFS_IS_NOTIFICATION_ENABLE, isNotificationEnable).apply()

    var isFirstTimeLaunch: Boolean
        get() = sharedPreferences.getBoolean(PREFS_IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTimeLaunch) = sharedPreferences.edit().putBoolean(PREFS_IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch).apply()

    fun clear() {
        val token = deviceToken
        sharedPreferences.edit().clear().apply()
        deviceToken = token
    }

    var image1: String
        get() = sharedPreferences.getString(PREFS_IMAGE_1, "")
        set(image1) = sharedPreferences.edit().putString(PREFS_IMAGE_1, image1).apply()

    var image2: String
        get() = sharedPreferences.getString(PREFS_IMAGE_2, "")
        set(image2) = sharedPreferences.edit().putString(PREFS_IMAGE_2, image2).apply()

    var image3: String
        get() = sharedPreferences.getString(PREFS_IMAGE_3, "")
        set(image3) = sharedPreferences.edit().putString(PREFS_IMAGE_3, image3).apply()

    var video1: String
        get() = sharedPreferences.getString(PREFS_VIDEO_1, "")
        set(video1) = sharedPreferences.edit().putString(PREFS_VIDEO_1, video1).apply()

    var video2: String
        get() = sharedPreferences.getString(PREFS_VIDEO_2, "")
        set(video2) = sharedPreferences.edit().putString(PREFS_VIDEO_2, video2).apply()

    var video3: String
        get() = sharedPreferences.getString(PREFS_VIDEO_3, "")
        set(video3) = sharedPreferences.edit().putString(PREFS_VIDEO_3, video3).apply()

    fun clearImage() {
        image1 = ""
        image2 = ""
        image3 = ""
        video1 = ""
        video2 = ""
        video3 = ""
    }
    /* public <T> void putData(String key, T value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        editor.apply();
    }*/


    //    public <T> T getData(String key, T defaultValue) {
    //        if (defaultValue instanceof Boolean) {
    //            return (T) (Boolean) (sharedPreferences.getBoolean(key, (Boolean) defaultValue));
    //        } else if (defaultValue instanceof String) {
    //            return (T) (String) (sharedPreferences.getString(key, (String) defaultValue));
    //        } else if (defaultValue instanceof Integer) {
    //            return (T) (Integer) (sharedPreferences.getInt(key, (Integer) defaultValue));
    //        }
    //        return null;
    //    }

}
