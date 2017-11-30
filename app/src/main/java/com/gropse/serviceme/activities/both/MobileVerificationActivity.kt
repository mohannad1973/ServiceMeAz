package com.gropse.serviceme.activities.both

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.HomeProviderActivity
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_verification.*


class MobileVerificationActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private val otpRequest = OtpRequest()
    private lateinit var signUpRequest: SignUpRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_verification)
        mActivity = this
        setUpToolbar(R.string.mobile_verification)

        if (intent.hasExtra(SignUpRequest::class.java.name))
            signUpRequest = intent.getSerializableExtra(SignUpRequest::class.java.name) as SignUpRequest

        etMobile.circularBorderDrawable(5)
        etOtp1.circularBorderDrawable(5)
        etOtp2.circularBorderDrawable(5)
        etOtp3.circularBorderDrawable(5)
        etOtp4.circularBorderDrawable(5)

        btnConfirm.circularDrawable()
        btnResend.circularDrawable()

        etOtp1.addTextChangedListener(GenericTextWatcher(etOtp1))
        etOtp2.addTextChangedListener(GenericTextWatcher(etOtp2))
        etOtp3.addTextChangedListener(GenericTextWatcher(etOtp3))
        etOtp4.addTextChangedListener(GenericTextWatcher(etOtp4))

        etMobile.setText(signUpRequest.mobile)
        otpRequest.mobile = signUpRequest.mobile

        btnResend.setOnClickListener {
            etOtp1.text.clear()
            etOtp2.text.clear()
            etOtp3.text.clear()
            etOtp4.text.clear()
            sendOtp()
        }
        btnConfirm.setOnClickListener {
            hideKeyboard()
            verifyOtp()
        }

        sendOtp()
    }

    private inner class GenericTextWatcher(val view: View) : TextWatcher {

        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.etOtp1 -> if (text.length == 1)
                    etOtp2.requestFocus()
                R.id.etOtp2 -> {
                    if (text.length == 1)
                        etOtp3.requestFocus()
                    if (text.isEmpty()) {
                        etOtp1.requestFocus()
                        if (etOtp1.text.isNotEmpty())
                            etOtp1.setSelection(1)
                    }
                }
                R.id.etOtp3 -> {
                    if (text.length == 1)
                        etOtp4.requestFocus()
                    if (text.isEmpty()) {
                        etOtp2.requestFocus()
                        if (etOtp2.text.isNotEmpty())
                            etOtp2.setSelection(1)
                    }
                }
                R.id.etOtp4 -> if (text.isEmpty()) {
                    etOtp3.requestFocus()
                    if (etOtp3.text.isNotEmpty())
                        etOtp3.setSelection(1)
                }
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

        }
    }

    private val isValidData: Boolean
        get() {
            otpRequest.otp = ""
            otpRequest.otp = etOtp1.string()
            otpRequest.otp += etOtp2.string()
            otpRequest.otp += etOtp3.string()
            otpRequest.otp += etOtp4.string()

            if (!isNetworkAvailable()) {
                return false
            } else if (otpRequest.otp.isBlank()) {
                toast(R.string.message_otp)
                return false
            } else if (otpRequest.otp.length < resources.getInteger(R.integer.otp_length)) {
                toast(R.string.message_otp_Valid)
                return false
            }
            return true
        }

    private fun verifyOtp() {
        if (isValidData) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.verifyOtp(otpRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response)
                    }) { throwable ->
                        onError(throwable)
                    }
            compositeDisposable?.add(disposable)
        }
    }

    private fun sendOtp() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.sendOtp(otpRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, ForgotResult::class.java.name)
                    }) { throwable ->
                        onError(throwable)
                    }
            compositeDisposable?.add(disposable)
        }
    }

    private fun signUpUser() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.signUpUser(signUpRequest)
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

    private fun signUpProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.signUpProvider(signUpRequest)
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

    private fun signUpSocialUser() {
        if (isNetworkAvailable()) {
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
                            Prefs(mActivity).image = bean.image
                            Prefs(mActivity).mobile = bean.mobile
                            Prefs(mActivity).userType = if (bean.providerType.isNotBlank()) bean.providerType.toInt() else AppConstants.TYPE_USER
                            Prefs(mActivity).securityToken = bean.securityToken

                            startActivity(Intent(mActivity, if (signUpRequest.providerType == AppConstants.TYPE_USER) HomeUserActivity::class.java else HomeProviderActivity::class.java))
                            finishAffinity()
                        } else if (response.obj.isJsonObject && resType == ForgotResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), ForgotResult::class.java)
                            otpRequest.securityToken = bean?.securityToken ?: ""
                        } else if (resType.isBlank()) {
                            if (signUpRequest.providerType == AppConstants.TYPE_USER) {
                                if (signUpRequest.type == AppConstants.LOGIN_TYPE_EMAIL) {
                                    signUpUser()
                                } else {
                                    signUpSocialUser()
                                }
                            } else {
                                signUpProvider()
                            }
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

}
