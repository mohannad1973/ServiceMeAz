package com.gropse.serviceme.socaillogin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.gropse.serviceme.R
import com.gropse.serviceme.socaillogin.OnSocialLoginListener
import com.gropse.serviceme.socaillogin.SocialBean
import com.gropse.serviceme.utils.AppConstants
import com.gropse.serviceme.utils.toast
import org.json.JSONObject
import java.util.*


class FacebookManager internal constructor(private val activity: Activity?, private var listener: OnSocialLoginListener?) : FacebookCallback<LoginResult> {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val loginManager: LoginManager? = LoginManager.getInstance()
    private val mProgressDialog: ProgressDialog = ProgressDialog(activity)

    init {
        mProgressDialog.setMessage(activity?.getString(R.string.message_loading))
        mProgressDialog.setCanceledOnTouchOutside(false)
        loginManager?.registerCallback(callbackManager, this)
    }

    fun doLogin() {
        if (loginManager != null) {
            loginManager.logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_friends"))
        } else {
            if (listener != null) {
                listener?.onSocialError("Login manager is not initialized")
            }
        }
    }

    fun doLogout(){
        loginManager?.logOut()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSuccess(result: LoginResult) {

        activity?.runOnUiThread { mProgressDialog.show() }
        val request = GraphRequest.newMeRequest(result.accessToken) { `object`, response ->
            try {
                val jsonObject = response.jsonObject
                fetchUserDetails(jsonObject.optString("id"))
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onSocialError(e.localizedMessage)
            } finally {
                activity?.runOnUiThread { mProgressDialog.dismiss() }
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id")
        request.parameters = parameters
        request.executeAsync()
    }

    override fun onCancel() {
        activity?.runOnUiThread {
            mProgressDialog.dismiss()
            activity.toast("Facebook bg_login was canceled")
        }
    }

    override fun onError(error: FacebookException) {
        activity?.runOnUiThread {
            activity.toast(error.localizedMessage)
        }
    }


    /**
     * Fetching user details from facebook.
     */
    private fun fetchUserDetails(id: String) {

        val parameters = Bundle()
        parameters.putString("fields", "id,first_name,last_name,email,picture,about,location")

        GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id,
                parameters,
                HttpMethod.GET
        ) { response ->
            try {
                loginManager?.logOut()
                val bean = SocialBean()
                bean.loginType = AppConstants.LOGIN_TYPE_FACEBOOK

                val jsonObject = response.jsonObject

                bean.id = jsonObject.optString("id")
                bean.fullName = jsonObject.optString("first_name") + " " + jsonObject.optString("last_name")
                bean.email = jsonObject.optString("email")

                val pic: JSONObject = if(jsonObject.optJSONObject("picture") != null)  jsonObject.optJSONObject("picture").getJSONObject("data") else JSONObject()

                bean.image = pic.optString("url")

                /* bean.setDevice_type(NetworkConstants.DEVICE_TYPE)
                                .setLogin_type(AppConstants.LOGIN_TYPE_FACEBOOK)
                                .setImage("")
                                .setCity("")
                                .setCountry("")
                                .setState("").setLogin_as("user").setMobile("").setPassword("")
                                .setCurrency("")
                                .setDevice_token(App.prefsOf(activity).getDT());*/


                if (listener != null) {
                    listener?.onSocialLogin(bean)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onSocialError(e.localizedMessage)
            } finally {
                activity?.runOnUiThread { mProgressDialog.dismiss() }
            }
        }.executeAsync()
    }
}
