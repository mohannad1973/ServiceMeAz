package com.gropse.serviceme.activities.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.activities.both.MobileVerificationActivity
import com.gropse.serviceme.activities.provider.VoucherActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.NetworkConstants
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.socaillogin.FacebookManager
import com.gropse.serviceme.socaillogin.OnSocialLoginListener
import com.gropse.serviceme.socaillogin.SocialBean
import com.gropse.serviceme.socaillogin.TwitterManager
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up_user.*
import kotlinx.android.synthetic.main.dialog_payment_layout.view.*
import kotlinx.android.synthetic.main.dialog_terms.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class SignUpUserActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private val signUpRequest = SignUpRequest()
    private val RC_SIGN_IN = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFacebookManager: FacebookManager? = null
    private var twitterManager: TwitterManager? = null
    private val PLACE_PICKER_REQUEST = 1

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_user)
        mActivity = this
        setUpToolbar(R.string.sign_up, true, R.color.colorPrimary)

        ivNext.circularDrawable()

        etFullName.circularBorderDrawable(5)
        etEmail.circularBorderDrawable(5)
        rlMobile.circularBorderDrawable(5)
        etPassword.circularBorderDrawable(5)
        etCity.circularBorderDrawable(5)
        tvLocation.circularBorderDrawable(5)

        etFullName.drawable(R.drawable.ic_user)
        etEmail.drawable(R.drawable.ic_email)
        etCode.drawable(R.drawable.ic_mobile)
        etPassword.drawable(R.drawable.ic_password)
        etCity.drawable(R.drawable.ic_pin)
        tvLocation.drawable(R.drawable.ic_pin)

        etCode.setText(Prefs(this).countryCode)
        etFullName.requestFocus()

        mFacebookManager = FacebookManager(mActivity, object : OnSocialLoginListener {
            override fun onSocialLogin(bean: SocialBean) {
                mFacebookManager?.doLogout()
                signUpRequest.type = AppConstants.LOGIN_TYPE_FACEBOOK
                signUpRequest.token = bean.id
                signUpRequest.image = bean.image
                etFullName.setText(bean.fullName)
                etEmail.setText(bean.email)
                ivProfile.loadUrl(bean.image)

                signUpRequest.isEmailVerified = if (etEmail.text.isBlank()) 0 else 1
                etEmail.isEnabled = signUpRequest.isEmailVerified == 0
                etPassword.gone()
            }

            override fun onSocialError(message: String) {

            }
        })

        twitterManager = TwitterManager(mActivity, AppConstants.TWITTER_CONSUMER_KEY, AppConstants.TWITTER_CONSUMER_SECRET, object : OnSocialLoginListener {
            override fun onSocialLogin(bean: SocialBean) {
                twitterManager?.doLogout()
                signUpRequest.type = AppConstants.LOGIN_TYPE_TWITTER
                signUpRequest.token = bean.id
                signUpRequest.email = bean.email
                etFullName.setText(bean.fullName)
                etEmail.setText(bean.email)
                ivProfile.loadUrl(bean.image)

                signUpRequest.isEmailVerified = if (etEmail.text.isBlank()) 0 else 1
                etEmail.isEnabled = signUpRequest.isEmailVerified == 0
                etPassword.gone()
            }

            override fun onSocialError(message: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        ivProfile.setOnClickListener { showImageChooser() }

        tvLocation.setOnClickListener {
            try {
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(mActivity), PLACE_PICKER_REQUEST)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        ivFacebook.setOnClickListener { mFacebookManager?.doLogin() }
        ivTwitter.setOnClickListener { twitterManager?.doLogin() }
        ivGoogle.setOnClickListener { startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN) }

        tv_terms.setOnClickListener {
            getTerms()
        }
        ivNext.setOnClickListener {
            if (signUpRequest.isEmailVerified == -1) {
                signUpRequest.type = AppConstants.LOGIN_TYPE_EMAIL
                signUpCheckUser()
            } else {
                signUpSocialUser()
            }
        }

//        startLocationRequest()
//        PermissionsManager.get()
//                .requestLocationPermission()
//                .subscribe { permissionsResult ->
//                    if (permissionsResult.isGranted) { // always true pre-M
//                    }
//                    if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
//                        // do whatever
//                    }
//                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            try {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    signUpRequest.type = AppConstants.LOGIN_TYPE_GOOGLE
                    signUpRequest.token = account?.id ?: ""
                    signUpRequest.image = account?.photoUrl.toString()
                    etFullName.setText(account?.displayName ?: "")
                    etEmail.setText(account?.email ?: "")
                    ivProfile.loadUrl(account?.photoUrl.toString())

                    signUpRequest.isEmailVerified = if (etEmail.text.isBlank()) 0 else 1
                    etEmail.isEnabled = signUpRequest.isEmailVerified == 0
                    etPassword.gone()
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient)
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
        signUpRequest.name = etFullName.string()
        signUpRequest.email = etEmail.string()
        signUpRequest.mobile = etCode.string().replace("+", "") + etMobile.string()
        signUpRequest.password = etPassword.string()
        signUpRequest.city = etCity.string()
        signUpRequest.location = tvLocation.string()
        signUpRequest.deviceType = NetworkConstants.DEVICE_TYPE
        signUpRequest.deviceId = Prefs(mActivity).deviceId
        signUpRequest.deviceToken = Prefs(mActivity).deviceToken
        if (!isNetworkAvailable(true)) {
            return false
        } else if (signUpRequest.type == AppConstants.LOGIN_TYPE_EMAIL && signUpRequest.image.isBlank()) {
            toast(R.string.message_select_profile_image)
            return false
        } else if (signUpRequest.name.isBlank()) {
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
        } else if (signUpRequest.type == AppConstants.LOGIN_TYPE_EMAIL && signUpRequest.password.isBlank()) {
            toast(R.string.message_enter_password)
            return false
        } else if (signUpRequest.type == AppConstants.LOGIN_TYPE_EMAIL && signUpRequest.password.length < resources.getInteger(R.integer.password_min) || signUpRequest.password.length > resources.getInteger(R.integer.password_max)) {
            toast(R.string.message_password_short)
            return false
        } else if (signUpRequest.location.isBlank()) {
            toast(R.string.message_select_location)
            return false
        } else if (signUpRequest.city.isBlank()) {
            toast(R.string.message_enter_city)
            return false
        } else if (!checkbox.isChecked) {
            toast(R.string.message_accept_terms)
            return false
        }
        return true
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

    private fun signUpSocialUser() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.signUpSocialUser(signUpRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, ProfileResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun signUpCheckUser() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.signUpCheckUser(signUpRequest)
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
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (response.obj.isJsonObject && resType == ProfileResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), ProfileResult::class.java)
                            Prefs(mActivity).login = true
                            Prefs(mActivity).userId = bean.id
                            Prefs(mActivity).name = bean.name
                            Prefs(mActivity).email = bean.email
                            Prefs(mActivity).image = bean.image
                            Prefs(mActivity).mobile = bean.mobile
                            Prefs(mActivity).userType = if (bean.providerType.isNotBlank()) bean.providerType.toInt() else AppConstants.TYPE_USER
                            Prefs(mActivity).securityToken = bean.securityToken

                            startActivity(Intent(mActivity, HomeUserActivity::class.java))
                            finishAffinity()
                        } else if (response.obj.isJsonObject && resType == FileResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), FileResult::class.java)
                            signUpRequest.image = bean?.id ?: ""
                        } else if (resType.isBlank()) {
                            val intent = Intent(mActivity, MobileVerificationActivity::class.java)
                            intent.putExtra(SignUpRequest::class.java.name, signUpRequest)
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

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {
        val rxLocation = RxLocation(this)

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

        compositeDisposable?.add(rxLocation.settings().checkAndHandleResolution(locationRequest)
                .subscribe { aBoolean ->
                    if (aBoolean) {
                        compositeDisposable?.add(rxLocation.location().updates(locationRequest)
                                .flatMap<Address> { location -> rxLocation.geocoding().fromLocation(location).toObservable() }
                                .subscribe { address ->
                                    signUpRequest.latitude = address.latitude.toString()
                                    signUpRequest.longitude = address.longitude.toString()
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }
    }

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

    private fun getTerms() {
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

    private fun onTermsResponse(response: TermsResponse) {
        when (response.errorCode) {
            1 -> logout()
            3 -> toast(response.message)
            200 -> {
                dialogTerms(response.result!!.data!!)
            }
        }
        progressBar.gone()
        frameLayout.gone()
    }
}
