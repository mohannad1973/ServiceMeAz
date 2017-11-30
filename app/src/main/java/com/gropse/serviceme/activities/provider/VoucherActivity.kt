package com.gropse.serviceme.activities.provider

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.SubscriptionRequest
import com.gropse.serviceme.pojo.VoucherResponse
import com.gropse.serviceme.pojo.VoucherResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_voucher.*

class VoucherActivity : BaseActivity() {

//    private var commonRequest = CommonRequest()
    private var subscriptionRequest = SubscriptionRequest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher)
        setUpToolbar(R.string.voucher)

        etVoucher.circularBorderDrawable(5)

        if (intent.hasExtra(SubscriptionRequest::class.java.name)) {
            subscriptionRequest = intent.getSerializableExtra(SubscriptionRequest::class.java.name) as SubscriptionRequest
        }

        btnProceed.setOnClickListener {
            if (etVoucher.text.isNotBlank()) {
                subscriptionRequest.code = etVoucher.string()
                useVoucherProvider()
            }
        }
    }

    private fun useVoucherProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.useVoucherProvider(Prefs(this).deviceId, Prefs(this).securityToken, subscriptionRequest)
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
            progressBar.gone()
            if (response is VoucherResponse) {
                toast(response.message)
                if (response.errorCode == 200) {
                    val bean = response.result ?: VoucherResult()
                    val intent = Intent()
                    intent.putExtra(VoucherResult::class.java.name, bean)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        progressBar.gone()
        t.printStackTrace()
        toast(R.string.message_error_connection)
    }
}
