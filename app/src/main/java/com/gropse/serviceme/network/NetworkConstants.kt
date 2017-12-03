package com.gropse.serviceme.network

object NetworkConstants {

    ///////////////////////////////////////////////////////////////////////////
    // Network Keys
    val DEVICE_TYPE = "0"//android
    val NETWORK_TAG = "network_log"

    ///////////////////////////////////////////////////////////////////////////

    private const val PROVIDER = "/provider"
    private const val USER = "/user"
    private const val LOGIN = "login"
    private const val FORGET_PASSWORD = "forget_password"
    private const val CHANGE_PASSWORD = "change_password"
    private const val UPDATE_PASSWORD = "update_password"
    private const val CATEGORIES = "categories"
    private const val GET_PROFILE = "get_profile"
    private const val EDIT_PROFILE = "edit_profile"
    private const val HOME = "home"
    private const val ONGOING = "ongoing"
    private const val COMPLETED = "completed"
    private const val FAVOURITES = "favourites"
    private const val CANCELLED = "cancelled"
    private const val FEEDBACK = "feedback"
    private const val UPDATE_STATUS = "update_status"
    private const val CURRENT_SERVICE = "current_service"


    ///////////////////////////////////////////////////////////////////////////

    const val TERMS = "terms"
    const val ABOUT_US = "aboutUs"
    const val CONTACT_US = "contactUs"
    const val API_SIGN_UP_CHECK_PROVIDER = "signUpCheck" + PROVIDER
    const val API_SIGN_UP_PROVIDER = "signUp" + PROVIDER
    const val API_LOGIN_PROVIDER = LOGIN + PROVIDER
    const val API_FORGET_PASSWORD_PROVIDER = FORGET_PASSWORD + PROVIDER
    const val API_UPDATE_PASSWORD_PROVIDER = UPDATE_PASSWORD + PROVIDER
    const val API_CHANGE_PASSWORD_PROVIDER = CHANGE_PASSWORD + PROVIDER
    const val API_CATEGORIES_PROVIDER = CATEGORIES + PROVIDER
    const val API_GET_PROFILE_PROVIDER = GET_PROFILE + PROVIDER
    const val API_EDIT_PROFILE_PROVIDER = EDIT_PROFILE + PROVIDER
    const val API_HOME_PROVIDER = HOME + PROVIDER
    const val API_READY_NOT_READY_PROVIDER = "ready_notready" + PROVIDER
    const val API_REQUESTS_PROVIDER = "requests" + PROVIDER
    const val API_SCHEDULED_PROVIDER = "scheduled" + PROVIDER
    const val API_COMPLETED_PROVIDER = COMPLETED + PROVIDER
    const val API_CANCELLED_PROVIDER = CANCELLED + PROVIDER
    const val API_ONGOING_PROVIDER = ONGOING + PROVIDER
    const val API_ACCEPT_PROVIDER = "accept" + PROVIDER
    const val API_REJECT_PROVIDER = "reject" + PROVIDER
    const val API_FEEDBACK_PROVIDER = FEEDBACK + PROVIDER
    const val API_UPDATE_STATUS_PROVIDER = UPDATE_STATUS + PROVIDER
    const val API_CURRENT_SERVICE_PROVIDER = CURRENT_SERVICE + PROVIDER
    const val API_SUBSCRIPTION_PLAN_PROVIDER = "subscription_plans" + PROVIDER
    const val API_UPDATE_SUBSCRIPTION_PROVIDER = "update_subscription" + PROVIDER
    const val API_VIEW_PROBLEM_PROVIDER = "view_problem" + PROVIDER
    const val API_USE_VOUCHER_PROVIDER = "use_voucher" + PROVIDER

    ///////////////////////////////////////////////////////////////////////////

    const val API_SIGN_UP_CHECK_USER = "sign_up_check" + USER
    const val API_SIGN_UP_USER = "sign_up" + USER
    const val API_SIGN_UP_SOCIAL_USER = "sign_up_social" + USER
    const val API_LOGIN_USER = LOGIN + USER
    const val API_LOGIN_SOCIAL_USER = "social_login" + USER
    const val API_FORGET_PASSWORD_USER = FORGET_PASSWORD + USER
    const val API_CHANGE_PASSWORD_USER = CHANGE_PASSWORD + USER
    const val API_UPDATE_PASSWORD_USER = UPDATE_PASSWORD + USER
    const val API_CATEGORIES_USER = CATEGORIES + USER
    const val API_GET_PROFILE_USER = GET_PROFILE + USER
    const val API_EDIT_PROFILE_USER = EDIT_PROFILE + USER
    const val API_HOME_USER = HOME + USER
    const val API_PENDING_USER = "pending" + USER
    const val API_ONGOING_USER = ONGOING + USER
    const val API_COMPLETED_USER = COMPLETED + USER
    const val API_FAVOURITES_USER = FAVOURITES + USER
    const val API_CANCELLED_USER = CANCELLED + USER
    const val API_FEEDBACK_USER = FEEDBACK + USER
    const val API_LIKE_USER = "like" + USER
    const val API_RESCHEDULE_USER = "reschedule" + USER
    const val API_ADD_SERVICE = "add_service" + USER
    const val API_UPDATE_STATUS_USER = UPDATE_STATUS + USER
    const val API_CURRENT_SERVICE_USER = CURRENT_SERVICE + USER
    const val API_REQUEST_SERVICE_USER = "request" + USER
    const val API_SERVICE_DETAILS_USER = "service_details" + USER

    ///////////////////////////////////////////////////////////////////////////

    const val API_SEND_OTP = "send_otp"
    const val API_VERIFY_OTP = "verify_otp"
    const val API_ADD_FILE = "add_file"
    const val API_LOGOUT = "logout"
}
