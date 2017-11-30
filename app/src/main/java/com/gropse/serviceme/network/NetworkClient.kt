package com.gropse.serviceme.network

import com.gropse.serviceme.pojo.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*

interface NetworkClient {

    @Multipart
    @POST(NetworkConstants.API_ADD_FILE)
    fun addFile(
            @Part("type") type: RequestBody,
            @Part file: MultipartBody.Part
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SIGN_UP_CHECK_PROVIDER)
    fun signUpCheckProvider(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SIGN_UP_CHECK_USER)
    fun signUpCheckUser(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SIGN_UP_PROVIDER)
    fun signUpProvider(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SIGN_UP_USER)
    fun signUpUser(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SIGN_UP_SOCIAL_USER)
    fun signUpSocialUser(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_LOGIN_PROVIDER)
    fun loginProvider(
            @Body request: LoginRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_LOGIN_USER)
    fun loginUser(
            @Body request: LoginRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_LOGIN_SOCIAL_USER)
    fun loginSocialUser(
            @Body request: LoginRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_FORGET_PASSWORD_PROVIDER)
    fun forgetPasswordProvider(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_FORGET_PASSWORD_USER)
    fun forgetPasswordUser(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_UPDATE_PASSWORD_PROVIDER)
    fun updatePasswordProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: ChangePassRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_UPDATE_PASSWORD_USER)
    fun updatePasswordUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: ChangePassRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CHANGE_PASSWORD_PROVIDER)
    fun changePasswordProvider(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CHANGE_PASSWORD_USER)
    fun changePasswordUser(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SEND_OTP)
    fun sendOtp(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_VERIFY_OTP)
    fun verifyOtp(
            @Body request: OtpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CATEGORIES_PROVIDER)
    fun categoryProvider(
            @Body jsonObject: JSONObject
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CATEGORIES_USER)
    fun categoryUser(
            @Body request: SignUpRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_GET_PROFILE_PROVIDER)
    fun getProfileProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_GET_PROFILE_USER)
    fun getProfileUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_EDIT_PROFILE_PROVIDER)
    fun editProfileProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: EditProfileRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_EDIT_PROFILE_USER)
    fun editProfileUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: EditProfileUserRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_HOME_PROVIDER)
    fun homeProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<HomeResponse>

    @POST(NetworkConstants.API_HOME_USER)
    fun homeUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<HomeResponse>

    @POST(NetworkConstants.API_READY_NOT_READY_PROVIDER)
    fun readyNotReadyProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<ReadyNotReadyResponse>

    @POST(NetworkConstants.API_REQUESTS_PROVIDER)
    fun requestsProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SCHEDULED_PROVIDER)
    fun scheduledProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_COMPLETED_PROVIDER)
    fun completedProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CANCELLED_PROVIDER)
    fun cancelledProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_ONGOING_PROVIDER)
    fun ongoingProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_ACCEPT_PROVIDER)
    fun acceptProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_REJECT_PROVIDER)
    fun rejectProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_FEEDBACK_PROVIDER)
    fun feedbackProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_FEEDBACK_USER)
    fun feedbackUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_UPDATE_STATUS_PROVIDER)
    fun statusProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_UPDATE_STATUS_USER)
    fun statusUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_PENDING_USER)
    fun pendingUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_ONGOING_USER)
    fun ongoingUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_COMPLETED_USER)
    fun completedUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CANCELLED_USER)
    fun cancelledUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_FAVOURITES_USER)
    fun favouritesUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_LIKE_USER)
    fun likeUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_RESCHEDULE_USER)
    fun rescheduleUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CURRENT_SERVICE_PROVIDER)
    fun currentServiceProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_CURRENT_SERVICE_USER)
    fun currentServiceUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SUBSCRIPTION_PLAN_PROVIDER)
    fun subscriptionPlan(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: SubscriptionRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_UPDATE_SUBSCRIPTION_PROVIDER)
    fun updateSubscription(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: SubscriptionRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_USE_VOUCHER_PROVIDER)
    fun useVoucherProvider(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: SubscriptionRequest
    ): Observable<VoucherResponse>

    @POST(NetworkConstants.API_ADD_SERVICE)
    fun addService(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: AddServiceRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_REQUEST_SERVICE_USER)
    fun requestService(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_VIEW_PROBLEM_PROVIDER)
    fun viewProblem(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<ViewProblemResponse>

    @POST(NetworkConstants.API_LOGOUT)
    fun logout(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>

    @POST(NetworkConstants.API_SERVICE_DETAILS_USER)
    fun serviceDetailsUser(
            @Header("device_id") deviceId: String,
            @Header("security_token") securityToken: String,
            @Body request: CommonRequest
    ): Observable<BaseResponse>
}