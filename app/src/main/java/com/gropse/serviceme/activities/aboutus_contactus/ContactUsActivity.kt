package com.gropse.serviceme.activities.aboutus_contactus

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Patterns
import android.view.View
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.ContactUsRequest
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var alertDialog: AlertDialog? = null

    private var request: ContactUsRequest = ContactUsRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        mActivity = this
        setUpToolbar(R.string.about_us)

        etName.circularBorderDrawable(5)

        etEmail.circularBorderDrawable(5)
        etPhone.circularBorderDrawable(5)
        etSubject.circularBorderDrawable(5)
        //etQuery.circularBorderDrawable(5)

        tvSubmit.circularDrawable()
        tvCall.circularDrawable()

        //etName.drawable(R.drawable.ic_user)


    }

    fun getContactUs() {
        if (isValidData() && isNetworkAvailable(true)) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.contactUs(request)
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

    private fun isValidData(): Boolean {
        request.name = etName.string()
        request.phone = etPhone.string()
        request.email = etEmail.string()
        request.subject = etSubject.string()
        request.query = etQuery.string()

        if (request.name.isBlank()) {
            toast(R.string.message_enter_name)
            return false
        } else if (request.phone.isBlank()) {
            toast(R.string.message_enter_phone)
            return false
        } else if (request.query.isBlank()) {
            toast(R.string.message_enter_query)
            return false
        } else if (request.subject.isBlank()) {
            toast(R.string.message_enter_subject)
            return false
        } else if (request.email.isBlank()) {
            toast(R.string.message_enter_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            toast(R.string.message_enter_valid_email)
            return false
        }
        return true
    }

    private fun onResponse(response: Any) {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        toast(response.message)
                        etName.text.clear()
                        etPhone.text.clear()
                        etEmail.text.clear()
                        etSubject.text.clear()
                        etQuery.text.clear()
                        request.name = etName.string()
                        request.phone = etPhone.string()
                        request.email = etEmail.string()
                        request.subject = etSubject.string()
                        request.query = etQuery.string()
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

    fun contactUs1(view: View) {
        getContactUs()
    }

    @SuppressLint("MissingPermission")
    fun callUs(view: View) {
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0987654321")))
    }
}