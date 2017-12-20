package com.gropse.serviceme.activities.provider

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.R
import com.gropse.serviceme.R.id.llCompany
import com.gropse.serviceme.R.id.tvServices
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.activities.both.LoginActivity
import com.gropse.serviceme.activities.both.MobileVerificationActivity
import com.gropse.serviceme.adapter.ServiceListAdapter
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.NetworkConstants
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.socaillogin.OnSocialLoginListener
import com.gropse.serviceme.socaillogin.SocialBean
import com.gropse.serviceme.utils.*
import com.gropse.serviceme.socaillogin.FacebookManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up_provider.*

import kotlinx.android.synthetic.main.dialog_terms.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignUpProviderActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private val signUpRequest = SignUpRequest()
    private val RC_SIGN_IN = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFacebookManager: FacebookManager? = null
    private val PLACE_PICKER_REQUEST = 1
    private var serviceList = ArrayList<CategoryResult>()

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_provider)
        mActivity = this
        setUpToolbar(R.string.sign_up)

        etName.circularBorderDrawable(5)
        rlMobile.circularBorderDrawable(5)
        etEmail.circularBorderDrawable(5)
        etPassword.circularBorderDrawable(5)
        etConfirmPassword.circularBorderDrawable(5)
        etCity.circularBorderDrawable(5)
        tvLocation.circularBorderDrawable(5)
        etRegistration.circularBorderDrawable(5)
        etWebsite.circularBorderDrawable(5)
        etLicense.circularBorderDrawable(5)
        tvServices.circularBorderDrawable(5)

        tvProceed.circularDrawable()

        etName.drawable(R.drawable.ic_user)
        etEmail.drawable(R.drawable.ic_email)
        etCode.drawable(R.drawable.ic_mobile)
        etPassword.drawable(R.drawable.ic_password)
        etConfirmPassword.drawable(R.drawable.ic_password)
        etCity.drawable(R.drawable.ic_pin)
        tvLocation.drawable(R.drawable.ic_pin)
        etRegistration.drawable(R.drawable.ic_reg)
        etLicense.drawable(R.drawable.ic_reg)
        etWebsite.drawable(R.drawable.ic_web)
        tvServices.drawable(R.drawable.ic_service)

       // etCode.setText(Prefs(this).countryCode)
        etName.requestFocus()
      //  etCode.setText(resources.configuration.locale.country)
        etCode.setText("+966");
        etCode.isEnabled=false

        tvLogin.spannableText(getString(R.string.already_have_an_account_login, getString(R.string.login)), getString(R.string.login))
        tvLogin.setOnClickListener({ startActivity(Intent(this, LoginActivity::class.java)) })

        mFacebookManager = FacebookManager(mActivity, object : OnSocialLoginListener {
            override fun onSocialLogin(bean: SocialBean) {
                mFacebookManager?.doLogout()
            }

            override fun onSocialError(message: String) {

            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */  /* OnConnectionFailedListener */) { _ -> toast("Google login error") }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        tvIndividual.circularLeftSideGradient()
        tvCompany.circularRightSideBorderGradient()
        tvIndividual.textColor(R.color.colorWhite)
        tvCompany.textColor(R.color.colorPrimary)
        llCompany.gone()
        signUpRequest.providerType = AppConstants.TYPE_PROVIDER_INDIVIDUAL
        etName.requestFocus()

        tvIndividual.setOnClickListener {
            tvIndividual.circularLeftSideGradient()
            tvCompany.circularRightSideBorderGradient()
            tvIndividual.textColor(R.color.colorWhite)
            tvCompany.textColor(R.color.colorPrimary)
            llCompany.gone()
            signUpRequest.providerType = AppConstants.TYPE_PROVIDER_INDIVIDUAL
            etName.drawable(R.drawable.ic_user)
            etName.hint = getString(R.string.full_name)
        }

        tvCompany.setOnClickListener {
            tvIndividual.circularLeftSideBorderGradient()
            tvCompany.circularRightSideGradient()
            tvIndividual.textColor(R.color.colorPrimary)
            tvCompany.textColor(R.color.colorWhite)
            llCompany.visible()
            signUpRequest.providerType = AppConstants.TYPE_PROVIDER_COMPANY
            etName.drawable(R.drawable.ic_company)
            etName.hint = getString(R.string.company_name)

//            if (Prefs(this).userType == 0)
//                signUpRequest.type = "individualProvider"
//
//            else if (Prefs(this).userType == 1)
//                signUpRequest.type = "companyProvider"
//
//            else
//                signUpRequest.type = "user"
        }

        ivProfile.setOnClickListener { showImageChooser() }

        tvLocation.setOnClickListener {
            try {
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(mActivity), PLACE_PICKER_REQUEST)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvServices.setOnClickListener {
            if (serviceList.size > 0) showServiceDialog() else categoryProvider()
        }

        tvProceed.setOnClickListener {
            signUpCheckProvider()
        }

//        ivFacebook.setOnClickListener { mFacebookManager?.doLogin() }
//        ivGoogle.setOnClickListener { startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN) }

        categoryProvider()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            try {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    val signUpBean = SocialBean()
                    signUpBean.loginType = AppConstants.LOGIN_TYPE_GOOGLE
                    signUpBean.uid = account?.id ?: ""
                    signUpBean.fullName = account?.displayName ?: ""
                    signUpBean.email = account?.email ?: ""
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient)
//                    login(signUpBean)
                    //                } else {
                    //                    AppUtility.showMessage(mActivity, "Google bg_login error");
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            val place = PlacePicker.getPlace(this, data)
            signUpRequest.latitude = place.latLng.latitude.toString()
            signUpRequest.longitude = place.latLng.longitude.toString()
            tvLocation.text = place.name
        } else {
            mFacebookManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showImageChooser() {
        val items = arrayOf("Camera", "Gallery")
        val adapter = ArrayAdapter(mActivity, android.R.layout.select_dialog_item, items)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setAdapter(adapter) { dialog, item ->
            if (item == 0) {
                ImageUtility.getImageBean(mActivity, ImageUtility.pickSingle(mActivity, ImageUtility.CAMERA))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            } else {
                ImageUtility.getImageBean(mActivity, ImageUtility.pickSingle(mActivity, ImageUtility.GALLERY))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setImage(bean: ImageUtility.ImageBean) {
        ivProfile.setImageBitmap(bean.bitmap)

        val type = RequestBody.create(MediaType.parse("text/plain"), "0")

        val file = File(bean.imagePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imageFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        addFile(type, imageFile)
    }

    private fun isValidData(): Boolean {
        signUpRequest.name = etName.string()
        signUpRequest.mobile = etCode.string().replace("+", "") + etMobile.string()
        signUpRequest.email = etEmail.string()
        signUpRequest.password = etPassword.string()
        signUpRequest.city = etCity.string()
        signUpRequest.location = tvLocation.string()
        signUpRequest.registrationNo = etRegistration.string()
        signUpRequest.website = etWebsite.string().trim()
        signUpRequest.licenseNo = etLicense.string()
//        signUpRequest.services = etServices.string()
        signUpRequest.deviceType = NetworkConstants.DEVICE_TYPE
        signUpRequest.deviceId = Prefs(mActivity).deviceId
        signUpRequest.deviceToken = Prefs(mActivity).deviceToken
        val confPass = etConfirmPassword.string()

        /*if (signUpRequest.image.isBlank()) {
            toast(R.string.message_select_profile_image)
            return false
        } else */
            if (signUpRequest.name.isBlank()) {
            toast(R.string.message_enter_name)
            return false
        } else if (signUpRequest.email.isBlank()) {
            toast(R.string.message_enter_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(signUpRequest.email).matches()) {
            toast(R.string.message_enter_valid_email)
            return false
        } else if (etMobile.string().isBlank()/* || signUpRequest.mobile.length < resources.getInteger(R.integer.mobile_length)*/) {
            toast(R.string.message_enter_valid_mobile)
            return false
        } else if (etCode.string().replace("+", "").isBlank()/* || signUpRequest.mobile.length < resources.getInteger(R.integer.mobile_length)*/) {
            toast(R.string.message_enter_country_code)
            return false
        } else if (signUpRequest.password.isBlank()) {
            toast(R.string.message_enter_password)
            return false
        } else if (signUpRequest.password.length < resources.getInteger(R.integer.password_min) || signUpRequest.password.length > resources.getInteger(R.integer.password_max)) {
            toast(R.string.message_password_short)
            return false
        } else if (!confPass.equals(signUpRequest.password, ignoreCase = true)) {
            toast(R.string.message_password_mismatch)
            return false
        } else if (signUpRequest.location.isBlank()) {
            toast(R.string.message_select_location)
            return false
        } else if (signUpRequest.city.isBlank()) {
            toast(R.string.message_enter_city)
            return false
        } else if (llCompany.visibility == 0 && signUpRequest.registrationNo.isBlank()) {
            toast(R.string.message_enter_reg_no)
            return false
        } else if (llCompany.visibility == 0 && !Patterns.WEB_URL.matcher(signUpRequest.website).matches()) {
            toast(R.string.message_enter_valid_website)
            return false
        } else if (signUpRequest.licenseNo.isBlank()) {
            toast(R.string.message_enter_license_no)
            return false
        } else if (signUpRequest.services.isBlank()) {
            toast(R.string.message_enter_service)
            return false
        } else if (!checkbox.isChecked) {
            toast(R.string.message_accept_terms)
            return false
        }
        return true
    }



    private fun showServiceDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_service_list, null)
        android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Select Services")
                .setView(view)
                .setPositiveButton(R.string.done, { dialogInterface, i ->
                    run {
                        dialogInterface.dismiss()
                        signUpRequest.services = ""
                        var serviceIds = ""
                        var serviceNames = ""
                        serviceList.forEach {
                            if (it.isChecked) {
                                serviceIds += it.id + ", "
                                serviceNames += it.name + ", "
                            }
                        }
                        signUpRequest.services = if (serviceIds.isNotBlank()) serviceIds.replace(", $".toRegex(), "") else ""
                        tvServices.text = if (serviceNames.isNotBlank()) serviceNames.replace(", $".toRegex(), "") else ""
                    }
                })
                .setNegativeButton(android.R.string.cancel, { dialogInterface, i ->
                    run {
                        dialogInterface.dismiss()
                    }
                }).create().show()
        val rvService = view.findViewById<RecyclerView>(R.id.rvService)
        rvService.layoutManager = LinearLayoutManager(mActivity)
        val serviceAdapter = ServiceListAdapter(object : ServiceListAdapter.OnItemClick {
            override fun onClick(bean: CategoryResult, position: Int) {
                bean.isChecked = !bean.isChecked
                rvService.adapter.notifyItemChanged(position)
            }
        })
        rvService.adapter = serviceAdapter
        serviceAdapter.addList(serviceList)
    }

    private fun addFile(type: RequestBody, file: MultipartBody.Part) {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.addFile(type, file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, FileResult::class.java.name)
                    }, { throwable ->
                        throwable.printStackTrace()
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun categoryProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.categoryProvider(JSONObject())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, CategoryResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun signUpCheckProvider() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.signUpCheckProvider(signUpRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun onResponse(response: Any, resType: String = "") {
        try {
            Log.d("usm_response","provider= "+response);
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (response.obj.isJsonArray && resType == CategoryResult::class.java.name) {
                            val bean = Gson().fromJson<ArrayList<CategoryResult>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<CategoryResult>>() {}.type)
                            serviceList.clear()
                            serviceList.addAll(bean)
                        } else if (response.obj.isJsonObject && resType == FileResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), FileResult::class.java)
                            signUpRequest.image = bean?.id ?: ""
                        } else if (resType.isBlank()) {
                            val intent = Intent(mActivity, MobileVerificationActivity::class.java)
                            intent.putExtra(SignUpRequest::class.java.name, signUpRequest)
                            intent.putExtra("isUser",false)
                            startActivity(intent)
                        }
                    }
                }
            }
            progressBar.gone()
            frameLayout.gone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        progressBar.gone()
        frameLayout.gone()
        toast(R.string.message_error_connection)
    }

//    private fun onResponse(response: Any) {
//        try {
//            var msg = ""
//            if (response is CategoryResponse) {
//                msg = response.message
//                if (response.errorCode == 200) {
//                    val bean = response.result ?: ArrayList()
//                    serviceList.clear()
//                    serviceList.addAll(bean)
//                    serviceList.forEach { it.image = "" }
//                }
//            } else if (response is FileResponse) {
//                msg = response.message
//                if (response.errorCode == 200) {
//                    val bean = response.result
//                    signUpRequest.image = bean?.id ?: ""
//                }
//            }
//            else if (response is BaseResponse){
//                if (response.errorCode == 200) {
//                    val intent = Intent(mActivity, MobileVerificationActivity::class.java)
//                    intent.putExtra(SignUpRequest::class.java.name, signUpRequest)
//                    startActivity(intent)
//                }
//            }
//            toast(msg)
//            progressBar.gone()
//            frameLayout.gone()
//        } catch (t: Throwable) {
//            onError(t)
//        }
//    }
//
//    private fun onError(t: Throwable) {
//        t.printStackTrace()
//        progressBar.gone()
//        frameLayout.gone()
//        toast(R.string.message_error_connection)
//
//    }

    private fun dialogTerms(terms: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_terms, null)
        dialogBuilder.setView(dialogView)
        dialogView.btnDone1.setOnClickListener {
            alertDialog?.dismiss()
        }
        dialogView.tvTerms.text = terms

        alertDialog = dialogBuilder.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    public fun getTerms(view: View) {
        if (isNetworkAvailable(true)) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.termsAndConditions()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onTermsResponse(response)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun onTermsResponse(response: BaseResponse) {
        when (response.errorCode) {
            1 -> logout()
            3 -> toast(response.message)
            200 -> {
                val bean = Gson().fromJson(response.obj.asJsonObject.toString(), TermsResult::class.java)
                dialogTerms(bean!!.data!!)
            }
        }
        progressBar.gone()
        frameLayout.gone()
    }
}
