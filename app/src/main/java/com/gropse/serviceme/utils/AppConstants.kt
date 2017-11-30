package com.gropse.serviceme.utils

import android.Manifest
import android.graphics.Bitmap

object AppConstants {


    val EMPTY_STRING = ""
    val LOGIN_TYPE_EMAIL = "0"
    val LOGIN_TYPE_FACEBOOK = "1"
    val LOGIN_TYPE_GOOGLE = "2"
    val LOGIN_TYPE_TWITTER = "3"
    val TWITTER_CONSUMER_KEY = "MCtYaqwun1A3JqTGSt1QclMLQ"
    val TWITTER_CONSUMER_SECRET = "rwsq0AMxZOOqvdg6KB5Axk4DbUKJNlnKxjqgTo0zQ5MWocKH0r"
    ///////////////////////////////////////////////////////////////////////////

    val SERVICE_FOR = "service_for"
    val SERVICE_HOME = "1"
    val SERVICE_OFFICE = "2"

    val SCREEN = "screen"
    val SERVICE_REQUEST = "1"
    val SCHEDULED_ORDERS = "2"
    val COMPLETED_ORDERS = "3"
    val ONGOING_ORDERS = "4"
    val CANCELLED_ORDERS = "5"
    val FAVOURITE_ORDERS = "6"
    val PENDING_ORDERS = "7"

    val RESET_PASSWORD = "1"
    val CHANGE_PASSWORD = "2"

    val FORGOT_PROVIDER = "1"
    val FORGOT_USER = "2"

    val TYPE_PROVIDER_INDIVIDUAL = 0
    val TYPE_PROVIDER_COMPANY = 1
    val TYPE_USER = 2

    val USER = "user"
    val PROVIDER = "provider"

    val KEY_USER_TYPE = "user_type"


    val VALUE_SELLER = "seller"
    val VALUE_USER = "user"

    val PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
    val IFSC_PATTERN = "[A-Z|a-z]{4}[0][a-zA-Z0-9]{6}$"
    val TEXT_PATTERN = "^[a-zA-Z ]*$"
    val NUMBER_PATTERN = "\\d+"

    // Request Code
    const val RC_ALL_NECESSARY_PERMISSION = 200
    val RC_FILE_READ_WRITE_PERMISSION_RESUME = 201
    val RC_FILE_READ_WRITE_PERMISSION_IMAGE = 202
    val RC_IMAGE_CAPTURE = 203
    val RC_CROP_PICTURE = 204
    val RC_LOCATION_PERMISSION = 205
    val RC_FILTER = 206
    val RC_CALL_PERMISSION = 207
    val RC_SELECT_CITY = 208
    val RC_HOME = 209
    val RC_GET_ACCOUNTS_PERMISSION = 210
    val RC_CATEGORY = 0
    val RC_MY_ACCOUNT = 1
    val RC_ACCOUNT = 2
    val RC_ORDER = 3
    val RC_EDIT_PROFILE = 214
    val RC_NEARBY = 215
    val RC_POPULAR = 216
    val RC_STORES = 217
    val RC_SUB_CATEGORIES_ITEM = 218
    val RC_WALLET = 219
    val RC_PAYMENT = 220
    val RC_NEW_POST = 221
    val RC_EDIT_POST = 222
    val ACTION_NOTHING = 900
    val RC_FORGOT = 300
    val RC_VERIFY = 300
    ///////////////////////////////////////////////////////////////////////////
    val ACTION_INCREMENT = 901
    val ACTION_DECREMENT = 902
    val ACTION_EDIT = 903
    val ACTION_CHAT = 904
    ///////////////////////////////////////////////////////////////////////////
    // Identity Code
    val TYPE_PICK_CAMERA = 5
    val TYPE_PICK_GALLERY = 6
    val TYPE_FORGOT_PASSWORD = 0
    val TYPE_CHANGE_PASSWORD = 1
    val TYPE_BUSINESS = 2
    val TYPE_EDUCATION = 3
    val TYPE_ADD_EVENT = 4
    val TYPE_EDIT_EVENT = 5
    val TYPE_OTHER_USER_PROFILE = 6
    ///////////////////////////////////////////////////////////////////////////
    // Permission Constants
    val PERMISSION_ALL_NECESSARY = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)


    val PERMISSION_FILE_READ_WRITE_IMAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    ///////////////////////////////////////////////////////////////////////////
    val PERMISSION_FILE_READ_WRITE_RESUME = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val PERMISSION_ACCESS_LOCATION = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val PERMISSION_CALL = arrayOf(Manifest.permission.CALL_PHONE)
    ///////////////////////////////////////////////////////////////////////////
    // Event Constants
    val EVENT_SIGN_UP_EMAIL_RESPONSE = 1
    ///////////////////////////////////////////////////////////////////////////
    val EVENT_LOGIN_EMAIL_RESPONSE = 2
    val EVENT_RESEND_OTP_RESPONSE = 3
    val EVENT_VERIFY_OTP_RESPONSE = 4
    val EVENT_FORGOT_PASS_RESPONSE = 5
    val EVENT_CHANGE_PASS_RESPONSE = 6
    ///////////////////////////////////////////////////////////////////////////
    // Extras
    val EXTRA_ID = "EXTRA_ID"
    ///////////////////////////////////////////////////////////////////////////
    val EXTRA_EMAIL_ID = "EXTRA_EMAIL_ID"
    val EXTRA_USER_ID = "EXTRA_USER_ID"
    val DISPLAY_NAME = "DISPLAY_NAME"
    val EXTRA_ACTIVITY_TYPE = "EXTRA_ACTIVITY_TYPE"
    val EXTRA_RESULT_STRING = "EXTRA_RESULT_STRING"
    val EXTRA_RESULT_DATA = "EXTRA_RESULT_DATA"
    val EXTRA_TITLE = "EXTRA_TITLE"
    val EXTRA_URL_LINK = "EXTRA_URL_LINK"
    val EXTRA_API_CALL = "EXTRA_API_CALL"
    val PUSH = "com.gropse.toohungry.push"
    val JOIN = "com.app.profantasy.join"
    val FINISH = "com.app.profantasy.finish"
    ///////////////////////////////////////////////////////////////////////////
    // Prefs_key
    val KEY_PREFS_NEW_JOB_ALERT = "KEY_PREFS_NEW_JOB_ALERT"
    ///////////////////////////////////////////////////////////////////////////
    val KEY_PREFS_PRIVATE_PROFILE = "KEY_PREFS_PRIVATE_PROFILE"
    ///////////////////////////////////////////////////////////////////////////
    // DateFormat
    val DATE_FORMAT_SERVER_TIMESTAMP = "yyyy-MM-dd HH:mm:ss"
    ///////////////////////////////////////////////////////////////////////////
    val DATE_FORMAT_APP = "dd/MM/yyyy"
    val MULTIPLE_IMAGE_LIMIT = 5
    ///////////////////////////////////////////////////////////////////////////
    val ONE_GO_DATA_LIMIT = 15
    var bitmap: Bitmap? = null

}
