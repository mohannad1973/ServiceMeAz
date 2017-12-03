package com.gropse.serviceme.pojo

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var obj: JsonElement
)

data class OrderResult(
        @SerializedName("image") var image: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("phone") var phone: String = "",
        @SerializedName("address") var address: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("description") var description: String = "",
        @SerializedName("distance") var distance: Double = 0.0,
        @SerializedName("ser_id") var serId: String = "",
        @SerializedName("status") var status: Int = 0,
        @SerializedName("ser_time") var serTime: Long = 0,
        @SerializedName("created_date") var createdDate: Long = 0,
        @SerializedName("ser_type") var serType: String = "",
        @SerializedName("reason") var reason: String = "",
        @SerializedName("is_favourite") var isFavourite: Int = 0,
        @SerializedName("latitude") var latitude: String = "",
        @SerializedName("longitude") var longitude: String = "",

        var timeLeft: Long = -1
) : Serializable

data class ForgotResult(
        @SerializedName("security_token") var securityToken: String = ""
)

data class HomeResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var result: HomeResult? = null
)

data class UserHomeResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var result: UserHomeResult? = null
)

data class UserHomeResult(
        @SerializedName("pending") var pending: Int = 0,

        @SerializedName("cancelled") var cancelled: Int = 0,

        @SerializedName("favourites") var favourites: Int = 0,

        @SerializedName("completed") var completed: Int = 0,

        @SerializedName("ongoing") var ongoing: Int = 0,

        @SerializedName("plan") var plan: PlanResult? = null,

        @SerializedName("category") var category: ArrayList<CategoryResult>? = null
)


data class HomeResult(
        @SerializedName("requests") var requests: Int = 0,
        @SerializedName("cancelled") var cancelled: Int = 0,
        @SerializedName("scheduled") var scheduled: Int = 0,
        @SerializedName("completed") var completed: Int = 0,
        @SerializedName("ongoing") var ongoing: Int = 0,
        @SerializedName("ready_status") var readyStatus: Int = 0,
        @SerializedName("plan") var plan: PlanResult? = null
)

data class PlanResult(
        @SerializedName("plan_start") var planStart: Long = 0,
        @SerializedName("plan_end") var planEnd: Long = 0,
        @SerializedName("plan_duration") var planDuration: Long = 0,
        @SerializedName("transaction_type") var transactionType: Int = 0,
        @SerializedName("transaction_id") var transactionId: String = ""
) : Serializable

data class ProfileResult(
        //FOR USER
        @SerializedName("id") var id: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("status") var status: Int = 0,
        @SerializedName("image") var image: String = "",
        @SerializedName("device_id") var deviceId: String = "",
        @SerializedName("security_token") var securityToken: String = "",

        //FOR PROVIDER
        @SerializedName("pro_type") var providerType: String = "",
        @SerializedName("registration_no") var registrationNo: String = "",
        @SerializedName("license_no") var licenseNo: String = "",
        @SerializedName("password") var password: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("city") var city: String = "",
        @SerializedName("website") var website: String = "",
        @SerializedName("created_date") var createdDate: String = "",
        @SerializedName("modified_date") var modifiedDate: String = "",

        @SerializedName("sub_plan") var subPlan: Int = 0,
        @SerializedName("sub_on") var subOn: Long = 0,
        @SerializedName("plan_time") var planTime: Long = 0,
        @SerializedName("transaction_type") var transactionType: Int = 0,
        @SerializedName("transaction_id") var transactionId: String = "",
        @SerializedName("service") var service: String = ""
)

data class CategoryResult(
        @SerializedName("id") var id: String = "",
        @SerializedName("cat_id") var catId: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("arabic_name") var arabicName: String = "",
        @SerializedName("urdu_name") var urduName: String = "",
        @SerializedName("russian_name") var russianName: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("description") var description: String = "",
        var isChecked: Boolean = false
) : Serializable

data class ReadyNotReadyResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var result: ReadyNotReadyResult? = null
)

data class ReadyNotReadyResult(
        @SerializedName("ready_status") var readyStatus: Int = 0
)

//data class FileResponse(
//        @SerializedName("isSuccess") var isSuccess: Boolean = false,
//        @SerializedName("errorCode") var errorCode: Int = 0,
//        @SerializedName("message") var message: String = "",
//        @SerializedName("Result") var result: FileResult? = null
//)

data class FileResult(
        @SerializedName("id") var id: String = ""
)

data class SubscriptionPlanResult(
        @SerializedName("id") var id: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("price") var price: String = "",
        @SerializedName("duration") var duration: Long = 0L,
        @SerializedName("type") var type: String = "",
        @SerializedName("created_date") var created_date: String = "",
        var isSelected: Boolean = false
)

/*overside fun compare(jc1: SubscriptionPlanResult, jc2: SubscriptionPlanResult): Int {
    return if (jc2.price < jc1.price)
        -1
    else
        if (jc2.price === jc1.price) 0 else 1
}*/

data class AddServiceResult(
        @SerializedName("providers") var result: ArrayList<Providers>? = null,
        @SerializedName("ser_id") var serId: String = ""
) : Serializable

data class Providers(
        @SerializedName("pro_id") var proId: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("mobile") var mobile: String = "",
        @SerializedName("email") var email: String = "",
        @SerializedName("location") var location: String = "",
        @SerializedName("latitude") var latitude: String = "",
        @SerializedName("longitude") var longitude: String = "",
        @SerializedName("created_date") var created_date: String = "",
        @SerializedName("pro_type") var providerType: Int = -1,
        @SerializedName("distance") var distance: Double = 0.0
) : Serializable

data class ViewProblemResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var result: ViewProblemResult? = null
)

data class ViewProblemResult(
        @SerializedName("description") var description: String = "",
        @SerializedName("files") var files: ArrayList<ViewProblemFiles>? = null
)

data class ViewProblemFiles(
        @SerializedName("url") var url: String = "",
        @SerializedName("type") var type: Int = 0
)

data class VoucherResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,
        @SerializedName("errorCode") var errorCode: Int = 0,
        @SerializedName("message") var message: String = "",
        @SerializedName("Result") var result: VoucherResult? = null
)

data class VoucherResult(
        @SerializedName("id") var id: String = "",
        @SerializedName("code") var code: String = "",
        @SerializedName("price") var price: Int = 0,
        @SerializedName("expire_after") var expireAfter: Long = 0L
) : Serializable

data class TermsResponse(
        @SerializedName("isSuccess") var isSuccess: Boolean = false,

        @SerializedName("errorCode") var errorCode: Int = 0,

        @SerializedName("message") var message: String? = null,

        @SerializedName("result") var result: TermsResult? = null
) : Serializable

data class TermsResult(
        @SerializedName("id") var id: String? = "",

        @SerializedName("data") var data: String? = "",

        @SerializedName("arabicData") var arabicData: String? = "",

        @SerializedName("urduData") var urduData: String? = "",

        @SerializedName("russianData") var russianData: String? = ""
) : Serializable
