package com.gropse.serviceme.activities.provider

//import paytabs.project.PayTabActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_view_problem_provider.*
import kotlinx.android.synthetic.main.dialog_payment_layout.view.*
import java.util.*
import kotlin.collections.ArrayList


class ViewProblemProviderActivity : BaseActivity() {
    private var alertDialog: AlertDialog? = null
    private var commonRequest = CommonRequest()
    private var orderResult = OrderResult()
    val REQUEST_CODE_PAYTABS = 456
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_problem_provider)
        setUpToolbar(R.string.view_problem_activity)
        orderResult = intent.getSerializableExtra(OrderResult::class.java.name) as OrderResult
        tvProviderName.text = orderResult.name
        ivProfile.loadUrl(orderResult.image)

        commonRequest.serId = orderResult.serId
        commonRequest.userId = Prefs(this).userId
        commonRequest.providerType = Prefs(this).userType
        commonRequest.transactionId = Prefs(this).transactionId

        btnAcceptRequest.setOnClickListener {
            //dialogPay()
            acceptProvider()
        }

        viewProblem()
    }

    private fun updateUI(bean: ViewProblemResult) {
        tvDescription.text = bean.description
        val beanArray = bean.files ?: ArrayList()
        val imageViews: ArrayList<ImageView> = ArrayList(Arrays.asList(ivImage1, ivImage2, ivImage3))
        val videoViews: ArrayList<ImageView> = ArrayList(Arrays.asList(ivVideo1, ivVideo2, ivVideo3))

        var imageList: ArrayList<String> = arrayListOf()
        var videoList: ArrayList<String> = arrayListOf()
        beanArray.forEachIndexed { index, viewProblemFiles ->
            run {
                if (viewProblemFiles.type == 0) {
                    imageList.add(viewProblemFiles.url)
                } else {
                    videoList.add(viewProblemFiles.url)
                }
            }
        }



        imageList.forEachIndexed { index, image ->
            run {
                imageViews[index].loadUrl(image)
                imageViews[index].setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(image)))
                }

            }
        }
        videoList.forEachIndexed { index, video ->
            run {
                videoViews[index].setImageDrawable(resources.getDrawable(R.drawable.ic_play))
                videoViews[index].setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(video)))

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYTABS) {
            val sharedPrefs = getSharedPreferences("paytabs_shared", MODE_PRIVATE)
            val responseCode = sharedPrefs.getString("pt_response_code", "")
            val transactionId = sharedPrefs.getString("pt_transaction_id", "")
            toast("PayTabs Response Code : " + responseCode)
            toast("Paytabs transaction ID after payment : " + transactionId)
            if (responseCode == "100") {
                commonRequest.transactionId = transactionId
                acceptProvider()
            } else {
                toast("Transaction Failed")
            }
        }
    }

    private fun dialogPay() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_payment_layout, null)
        dialogBuilder.setView(dialogView)
        dialogView.btnVoucher.setOnClickListener {
            val intent = Intent(this@ViewProblemProviderActivity, VoucherActivity::class.java)
            intent.putExtra(OrderResult::class.java.name, orderResult)
            startActivity(intent)
            alertDialog?.dismiss()
        }
        dialogView.btnCard.setOnClickListener {
            //            payTabs()
            alertDialog?.dismiss()
        }

        alertDialog = dialogBuilder.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    private fun viewProblem() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.viewProblem(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun acceptProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.acceptProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
            if (response is ViewProblemResponse) {
                toast(response.message)
                if (response.errorCode == 200) {
                    scrollView.visible()
                    val bean = response.result ?: ViewProblemResult()
                    updateUI(bean)
                }
            }
            if (response is BaseResponse) {
                toast(response.message)
                val brodCastIntent = Intent()
                brodCastIntent.action = AppConstants.FINISH
                sendBroadcast(brodCastIntent)
                finish()
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


//    private fun payTabs() {
//        val intent = Intent(this@ViewProblemProviderActivity, PayTabActivity::class.java)
//        intent.putExtra("pt_merchant_email", "mohannad@konozstore.com") //this a demo account for testing the sdk
//        intent.putExtra("pt_secret_key", "mHdyDzGHkGyFM7PrJj29ogjtY8FuqKvNsIm5i2NDJmivt8Xcr0lQgEkeNZIeJe1cWoifq5S9raFViD8KDuBT8V8WVRbF5cR1B9h1")//Add your Secret Key Here
//        intent.putExtra("pt_transaction_title", "Subscription")
//        intent.putExtra("pt_amount", "5")
//        intent.putExtra("pt_currency_code", "SAR") //Use Standard 3 character ISO
//        intent.putExtra("pt_shared_prefs_name", "paytabs_shared")
//        intent.putExtra("pt_customer_email", "mohannad@konozstore.com")
//        intent.putExtra("pt_customer_phone_number", "00966554055502")
//        intent.putExtra("pt_order_id", "1234567")
//        intent.putExtra("pt_product_name", "RequestAcceptingFee")
//        intent.putExtra("pt_timeout_in_seconds", "60") //Optional
//
//        //Billing Address
//        intent.putExtra("pt_address_billing", "Saudi Arabia")
//        intent.putExtra("pt_city_billing", "Dammam")
//        intent.putExtra("pt_state_billing", "Dammam")
//        intent.putExtra("pt_country_billing", "SAR")
//        intent.putExtra("pt_postal_code_billing", "966") //Put Country Phone code if Postal code not available '00973'
//
//        //Shipping Address
//        intent.putExtra("pt_address_shipping", "Saudi Arabia")
//        intent.putExtra("pt_city_shipping", "Dammam")
//        intent.putExtra("pt_state_shipping", "Dammam")
//        intent.putExtra("pt_country_shipping", "SAR")
//        intent.putExtra("pt_postal_code_shipping", "966") //Put Country Phone code if Postal code not available '00973'
//        startActivityForResult(intent, REQUEST_CODE_PAYTABS)
//    }
}
