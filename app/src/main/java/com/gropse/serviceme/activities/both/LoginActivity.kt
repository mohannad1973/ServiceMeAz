package com.gropse.serviceme.activities.both

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.HomeProviderActivity
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.NetworkConstants
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.LoginRequest
import com.gropse.serviceme.pojo.ProfileResult
import com.gropse.serviceme.socaillogin.FacebookManager
import com.gropse.serviceme.socaillogin.OnSocialLoginListener
import com.gropse.serviceme.socaillogin.SocialBean
import com.gropse.serviceme.socaillogin.TwitterManager
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import net.ralphpina.permissionsmanager.PermissionsManager


class LoginActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private val loginRequest = LoginRequest()
    private var isUser: Boolean = true
    private val RC_SIGN_IN = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFacebookManager: FacebookManager? = null
    private var twitterManager: TwitterManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mActivity = this
        setUpToolbar(R.string.login, true, R.color.colorPrimary)

        tvLogin.circularDrawable()

        etEmail.circularBorderDrawable(5)
        etPassword.circularBorderDrawable(5)

        tvSignUp.spannableText(getString(R.string.new_to_service_me, getString(R.string.sign_up)), getString(R.string.sign_up))

        tvUser.circularLeftSideGradient()
        tvProvider.circularRightSideBorderGradient()
        tvUser.textColor(R.color.colorWhite)
        tvProvider.textColor(R.color.colorPrimary)

        etEmail.drawable(R.drawable.ic_email)
        etPassword.drawable(R.drawable.ic_password)

        Prefs(mActivity).deviceToken = FirebaseInstanceId.getInstance().token ?: ""

        mFacebookManager = FacebookManager(mActivity, object : OnSocialLoginListener {
            override fun onSocialLogin(bean: SocialBean) {
                mFacebookManager?.doLogout()
                loginRequest.type = AppConstants.LOGIN_TYPE_FACEBOOK
                loginRequest.token = bean.id
                loginRequest.email = bean.email
                loginSocialUser()
            }

            override fun onSocialError(message: String) {

            }
        })

        twitterManager = TwitterManager(mActivity, AppConstants.TWITTER_CONSUMER_KEY, AppConstants.TWITTER_CONSUMER_SECRET, object : OnSocialLoginListener{
            override fun onSocialLogin(bean: SocialBean) {
                twitterManager?.doLogout()
                loginRequest.type = AppConstants.LOGIN_TYPE_TWITTER
                loginRequest.token = bean.id
                loginRequest.email = bean.email
                loginSocialUser()
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
                .enableAutoManage(this /* FragmentActivity */  /* OnConnectionFailedListener */) { connectionResult -> toast("Google login error") }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        tvUser.setOnClickListener {
            tvUser.circularLeftSideGradient()
            tvProvider.circularRightSideBorderGradient()
            tvUser.textColor(R.color.colorWhite)
            tvProvider.textColor(R.color.colorPrimary)
            isUser = true
            llSocial.visible()
        }

        tvProvider.setOnClickListener {
            tvUser.circularLeftSideBorderGradient()
            tvProvider.circularRightSideGradient()
            tvUser.textColor(R.color.colorPrimary)
            tvProvider.textColor(R.color.colorWhite)
            isUser = false
            llSocial.gone()
        }

        ivFacebook.setOnClickListener { mFacebookManager?.doLogin() }
        ivTwitter.setOnClickListener { twitterManager?.doLogin() }
        ivGoogle.setOnClickListener { startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN) }

        tvLogin.setOnClickListener {
            loginRequest.type = AppConstants.LOGIN_TYPE_EMAIL
            if (isUser) {
                loginUser()
            } else {
                loginProvider()
            }
        }
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, OptionActivity::class.java))
            finishAffinity()
        }
        tvForget.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, if (isUser) AppConstants.FORGOT_USER else AppConstants.FORGOT_PROVIDER)
            startActivity(intent)
        }

        PermissionsManager.get()
                .requestLocationPermission()
                .subscribe { permissionsResult ->
                    if (permissionsResult.isGranted) { // always true pre-M
                        startLocationRequest()
                    }
                    if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
                        // do whatever
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            try {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    loginRequest.type = AppConstants.LOGIN_TYPE_GOOGLE
                    loginRequest.token = account?.id ?: ""
                    loginRequest.email = account?.email ?: ""
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                    loginSocialUser()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mFacebookManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isValidData(): Boolean {
        loginRequest.email = if (loginRequest.type == AppConstants.LOGIN_TYPE_EMAIL) etEmail.string() else loginRequest.email
        loginRequest.password = etPassword.string()
        loginRequest.deviceType = NetworkConstants.DEVICE_TYPE
        loginRequest.deviceId = Prefs(mActivity).deviceId
        loginRequest.deviceToken = Prefs(mActivity).deviceToken
        if (!isNetworkAvailable(true)) {
            return false
        } else if (loginRequest.email.isBlank()) {
            toast(R.string.message_enter_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginRequest.email).matches()) {
            toast(R.string.message_enter_valid_email)
            return false
        } else if (loginRequest.type == AppConstants.LOGIN_TYPE_EMAIL && loginRequest.password.isBlank()) {
            toast(R.string.message_enter_password)
            return false
        } else if (loginRequest.type == AppConstants.LOGIN_TYPE_EMAIL && loginRequest.password.length < resources.getInteger(R.integer.password_min) || loginRequest.password.length > resources.getInteger(R.integer.password_max)) {
            toast(R.string.message_password_invalid)
            return false
        }
        return true
    }

    private fun loginUser() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.loginUser(loginRequest)
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

    private fun loginSocialUser() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.loginSocialUser(loginRequest)
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

    private fun loginProvider() {
        if (isValidData()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.loginProvider(loginRequest)
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

    private fun onResponse(response: Any) {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (response.obj.isJsonObject) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), ProfileResult::class.java)
                            Prefs(mActivity).login = true
                            Prefs(mActivity).userId = bean.id
                            Prefs(mActivity).name = bean.name
                            Prefs(mActivity).image = bean.image
                            Prefs(mActivity).mobile = bean.mobile
                            Prefs(mActivity).userType = if (bean.providerType.isNotBlank()) bean.providerType.toInt() else AppConstants.TYPE_USER
                            Prefs(mActivity).securityToken = bean.securityToken

                            startActivity(Intent(mActivity, if (isUser) HomeUserActivity::class.java else HomeProviderActivity::class.java))
                            finishAffinity()
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
                                    loginRequest.latitude = address.latitude.toString()
                                    loginRequest.longitude = address.longitude.toString()
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }
    }
}
