package com.gropse.serviceme.activities.aboutus_contactus

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.TermsResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        mActivity = this
        setUpToolbar(R.string.about_us)

        getAboutUs()
    }

    fun getAboutUs() {
        if (isNetworkAvailable(true)) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.aboutUs()
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
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), TermsResult::class.java)
                            when (Prefs(this).locale) {
                                "en" -> tvAboutus.text = bean!!.data
                                "ar" -> tvAboutus.text = bean!!.arabicData
                                "ur" -> tvAboutus.text = bean!!.urduData
                                "tr" -> tvAboutus.text = bean!!.data
                                "ru" -> tvAboutus.text = bean!!.russianData
                                else -> tvAboutus.text = bean!!.data
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