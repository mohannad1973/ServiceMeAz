package com.gropse.serviceme.activities.both

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.gropse.serviceme.R
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_review_rating.*
import kotlinx.android.synthetic.main.dialog_feedback.view.*

class ReviewRatingActivity : BaseActivity() {
    private var commonRequest = CommonRequest()
    var dialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_rating)
        setUpToolbar(R.string.review_rating)

        if (intent.hasExtra(OrderResult::class.java.name)) {
            val orderResult = intent.getSerializableExtra(OrderResult::class.java.name) as OrderResult
            commonRequest.serId = orderResult.serId
            ivProfile.loadUrl(orderResult.image)
            tvName.text = orderResult.name
        }
        llAngry.setOnClickListener {
            reset()
            ivAngry.setImageResource(R.drawable.angry_selected)
            tvAngry.textColor(R.color.colorPrimary)
            commonRequest.rate = 1
            dialog()
        }
        llSad.setOnClickListener {
            reset()
            ivSad.setImageResource(R.drawable.sad_selected)
            tvSad.textColor(R.color.colorPrimary)
            commonRequest.rate = 2
            dialog()
        }
        llUnsure.setOnClickListener {
            reset()
            ivUnsure.setImageResource(R.drawable.unsure_selected)
            tvUnsure.textColor(R.color.colorPrimary)
            commonRequest.rate = 3
            dialog()
        }
        llHappy.setOnClickListener {
            reset()
            ivHappy.setImageResource(R.drawable.happy_selected)
            tvHappy.textColor(R.color.colorPrimary)
            commonRequest.rate = 4
            dialog()
        }
        llAwesome.setOnClickListener {
            reset()
            ivAwesome.setImageResource(R.drawable.awesome_selected)
            tvAwesome.textColor(R.color.colorPrimary)
            commonRequest.rate = 5
            dialog()
        }
    }

    private fun reset() {
        ivAngry.setImageResource(R.drawable.angry)
        ivSad.setImageResource(R.drawable.sad)
        ivUnsure.setImageResource(R.drawable.unsure)
        ivHappy.setImageResource(R.drawable.happy)
        ivAwesome.setImageResource(R.drawable.awesome)

        tvAngry.textColor()
        tvSad.textColor()
        tvUnsure.textColor()
        tvHappy.textColor()
        tvAwesome.textColor()
    }


    private fun dialog() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_feedback, null)
        dialogBuilder?.setView(dialogView)
        dialogView.btnSend.setOnClickListener {
            alertDialog?.dismiss()
            commonRequest.feedback = dialogView.etFeedback.text.toString()
            if (intent.hasExtra(AppConstants.SCREEN)) {
                if (intent.getStringExtra(AppConstants.SCREEN) == AppConstants.PROVIDER) {
                    feedbackProvider()
                } else {
                    feedbackUser()
                }
            }
        }

        alertDialog = dialogBuilder?.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }


    private fun feedbackProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.feedbackProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun feedbackUser() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.feedbackUser(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
                    200 -> finish()
                }
            }
            progressBar.gone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        progressBar.gone()
        toast(R.string.message_error_connection)
    }


}
