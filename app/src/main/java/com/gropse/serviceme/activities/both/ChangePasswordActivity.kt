package com.gropse.serviceme.activities.both

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gropse.serviceme.R
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.ChangePassRequest
import com.gropse.serviceme.pojo.ForgotResult
import com.gropse.serviceme.pojo.OtpRequest
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_password.*
import org.json.JSONObject

class ChangePasswordActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private var otpRequest = OtpRequest()
    private var changePassRequest = ChangePassRequest()
    var type = AppConstants.RESET_PASSWORD
    var userType = AppConstants.TYPE_USER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        mActivity = this
        setUpToolbar(R.string.change_password)

        etOldPassword.circularBorderDrawable(5)
        etNewPassword.circularBorderDrawable(5)
        etConfirmPassword.circularBorderDrawable(5)


        if (intent.hasExtra(AppConstants.TYPE)) {
            userType = intent.getIntExtra(AppConstants.TYPE,AppConstants.TYPE_USER)
        }

        btnDone.circularDrawable()



        btnDone.setOnClickListener {
            println("úsm_user_type= "+type+"',userType= "+userType);
           // if (Prefs(mActivity).userType == AppConstants.TYPE_USER)
                if (userType == AppConstants.TYPE_USER)
            {
                if (type == AppConstants.RESET_PASSWORD) {
                    changePasswordUser()
                } else {
                    updatePasswordUser()
                }
            } else {
                if (type == AppConstants.RESET_PASSWORD) {
                    changePasswordProvider()
                } else {
                    updatePasswordProvider()
                }
            }

        }
        if (intent.hasExtra(OtpRequest::class.java.name))
            otpRequest = intent.getSerializableExtra(OtpRequest::class.java.name) as OtpRequest
        if (intent.hasExtra(AppConstants.SCREEN)) {
            type = intent.getStringExtra(AppConstants.SCREEN)
            if (type == AppConstants.RESET_PASSWORD) etOldPassword.gone()
        }
    }

    private val isValid: Boolean
        get() {
            changePassRequest.oldPassword = etOldPassword.string()
            changePassRequest.newPassword = etNewPassword.string()
            otpRequest.password = etConfirmPassword.string()

            if (!isNetworkAvailable()) {
                return false
            } else if (type == AppConstants.CHANGE_PASSWORD && changePassRequest.oldPassword.isBlank()) {
                toast(R.string.message_password_short)
                return false
            } else if (changePassRequest.newPassword.length < resources.getInteger(R.integer.password_min)) {
                toast(R.string.message_password_short)
                return false
            } else if (!changePassRequest.newPassword.equals(otpRequest.password, ignoreCase = true)) {
                toast(R.string.message_password_mismatch)
                return false
            }
            otpRequest.email = if (otpRequest.email.isNotBlank()) otpRequest.email else Prefs(this).email
            otpRequest.mobile = if (otpRequest.mobile.isNotBlank()) otpRequest.mobile else Prefs(this).mobile
            changePassRequest.userId = Prefs(this).userId
            changePassRequest.providerType = Prefs(this).userType


            println("úsm_otpRequest : mobile= "+otpRequest.mobile+"',password= "+otpRequest.password);

            return true
        }

    private fun changePasswordProvider() {
        println("úsm_otpRequestprovider_before : mobile= "+otpRequest.mobile+"',password= "+otpRequest.password);
        if (isValid) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.changePasswordProvider(otpRequest)
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

    private fun updatePasswordProvider() {
        println("úsm_otpRequest_provider_update_before : mobile= "+otpRequest.mobile+"',password= "+otpRequest.password);
        if (isValid) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.updatePasswordProvider(Prefs(this).deviceId, Prefs(this).securityToken, changePassRequest)
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

    private fun changePasswordUser() {
        println("úsm_otpRequest_before : mobile= "+otpRequest.mobile+"',password= "+otpRequest.password);
        if (isValid) {
            println("úsm_otpRequest : mobile= "+otpRequest.mobile+"',password= "+otpRequest.password);
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.changePasswordUser(otpRequest)
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

    private fun updatePasswordUser() {
        if (isValid) {
            println("úsm_user_update : Password is updating"+userType);
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.updatePasswordUser(Prefs(this).deviceId, Prefs(this).securityToken, changePassRequest)
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

    private fun onResponse(response: Any) {
        try {

          //  val response = Gson().fromJson(removeHtmlTags(responseStr.toString()), BaseResponse::class.java)
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (type == AppConstants.RESET_PASSWORD) {
                            val intent = Intent(mActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            finish()
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
