package com.gropse.serviceme.activities.both

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private val otpRequest = OtpRequest()
    private var type = AppConstants.FORGOT_PROVIDER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        mActivity = this
        setUpToolbar(R.string.forgot_pass)

        otpRequest.lang = Prefs(this).locale

        if (intent.hasExtra(AppConstants.SCREEN)) {
            type = intent.getStringExtra(AppConstants.SCREEN)
        }

        llMobile.circularBorderDrawable(5)
        etOtp1.circularBorderDrawable(5)
        etOtp2.circularBorderDrawable(5)
        etOtp3.circularBorderDrawable(5)
        etOtp4.circularBorderDrawable(5)

        btnConfirm.circularDrawable()
        ivNext.circularDrawable()

        etOtp1.addTextChangedListener(GenericTextWatcher(etOtp1))
        etOtp2.addTextChangedListener(GenericTextWatcher(etOtp2))
        etOtp3.addTextChangedListener(GenericTextWatcher(etOtp3))
        etOtp4.addTextChangedListener(GenericTextWatcher(etOtp4))

        ivNext.setOnClickListener {
            if (etMobile.string().isNotBlank()) {
                otpRequest.mobile = etMobile.string()
                if (type == AppConstants.FORGOT_PROVIDER) forgetPasswordProvider() else forgetPasswordUser()
            }
        }
        btnConfirm.setOnClickListener { verifyOtp() }
    }

    private inner class GenericTextWatcher(private val view: View) : TextWatcher {

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
            // TODO Auto-generated method stub
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            // TODO Auto-generated method stub
        }
    }

    private val isValidData: Boolean
        get() {
            otpRequest.otp = ""
            otpRequest.otp = otpRequest.otp + etOtp1.string()
            otpRequest.otp = otpRequest.otp + etOtp2.string()
            otpRequest.otp = otpRequest.otp + etOtp3.string()
            otpRequest.otp = otpRequest.otp + etOtp4.string()

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

    private fun forgetPasswordProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.forgetPasswordProvider(otpRequest)
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

    private fun forgetPasswordUser() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.forgetPasswordUser(otpRequest)
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



    private fun onResponse(response: Any, resType: String = "") {
        try {

            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {

                        if (response.obj.isJsonObject && resType == ForgotResult::class.java.name) {

                            val bean = Gson().fromJson(removeHtmlTags(response.obj.asJsonObject.toString()), ForgotResult::class.java)
                            otpRequest.securityToken = bean?.securityToken ?: ""
                        } else if (resType.isBlank()){
                            val intent = Intent(mActivity, ChangePasswordActivity::class.java)
                            intent.putExtra(OtpRequest::class.java.name, otpRequest)
                            intent.putExtra(AppConstants.SCREEN, AppConstants.RESET_PASSWORD)
                            intent.putExtra(AppConstants.TYPE, type)
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
}
