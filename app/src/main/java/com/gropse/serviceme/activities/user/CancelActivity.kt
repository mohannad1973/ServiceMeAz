package com.gropse.serviceme.activities.user

import android.os.Bundle
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cancel.*

class CancelActivity : BaseActivity() {

    private var commonRequest = CommonRequest()
    private var orderResult = OrderResult()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel)
        setUpToolbar(R.string.service_cancel)

        if (intent.hasExtra(OrderResult::class.java.name)) {
            orderResult = intent.getSerializableExtra(OrderResult::class.java.name) as OrderResult
            commonRequest.serId = orderResult.serId
            commonRequest.status = 2
            updateUI(orderResult)
        }

        btnCancel.circularDrawable()
        btnCancel.setOnClickListener { statusUser() }
    }

    private fun updateUI(bean: OrderResult){
        ivProvider.loadUrl(bean.image)
        tvName.text = bean.name
        tvDistance.roundDecimal(bean.distance)
        tvPhoneNumber.text = bean.phone
    }

    private fun statusUser() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.statusUser(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
                toast(response.message)
                finish()
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
