package com.gropse.serviceme.activities.both

//import paytabs.project.PayTabActivity
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.VoucherActivity
import com.gropse.serviceme.adapter.SubscriptionPlanAdapter
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_subscription_plan.*
import kotlinx.android.synthetic.main.dialog_payment_layout.view.*
import java.util.*
import kotlin.collections.ArrayList


class SubscriptionPlanActivity : BaseActivity() {

    private var alertDialog: AlertDialog? = null
    private var planList = ArrayList<SubscriptionPlanResult>()
    private lateinit var subscriptionPlanAdapter: SubscriptionPlanAdapter
    private var subscriptionPlanResult = SubscriptionPlanResult()
    private var subscriptionRequest = SubscriptionRequest()
    val REQUEST_CODE_VOUCHER = 123
    val REQUEST_CODE_PAYTABS = 456
    private val PAY = 0
    private val VOUCHER = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_plan)
        setUpToolbar(R.string.subscription)

        if (intent.hasExtra(PlanResult::class.java.name)) {
            val plan = intent.getSerializableExtra(PlanResult::class.java.name) as PlanResult
            llParent.visible()
            llPlan.visible()
            tvDuration.formatMillisDateTime(plan.planStart)
            tvStartDate.formatMillisDateTime(plan.planStart)
            tvEndDate.formatMillisDateTime(plan.planEnd)
            tvPayType.text = if (plan.transactionType == VOUCHER) getString(R.string.voucher) else getString(R.string.online)

            //tvPayNow.gone()
            tvPayNow.text = resources.getText(R.string.str_renew)
        } else {
            //subscriptionPlan()
        }

        subscriptionRequest.userId = Prefs(this).userId
        subscriptionRequest.providerType = Prefs(this).userType
        subscriptionRequest.type = when (Prefs(this).userType) {
            2 -> 1
            0 -> 0
            1 -> 2
            else -> 1
        }

        subscriptionPlanAdapter = SubscriptionPlanAdapter(object : SubscriptionPlanAdapter.OnItemClick {
            override fun onClick(bean: SubscriptionPlanResult, position: Int) {
                subscriptionPlanResult = bean
                subscriptionRequest.planId = bean.id
                planList.forEach { it.isSelected = false }
                bean.isSelected = true
                subscriptionPlanAdapter.notifyDataSetChanged()
            }
        })

        rvSubscriptionPlan.layoutManager = GridLayoutManager(this, 2)
        rvSubscriptionPlan.adapter = subscriptionPlanAdapter

        tvPayNow.circularDrawable()
        tvPayNow.drawable(end = R.drawable.ic_chevron_right_black_24dp)
        tvPayNow.setOnClickListener {
            if (tvPayNow.text.equals(resources.getText(R.string.str_renew))) {
                selectPlan()
            } else if (tvPayNow.text.equals(resources.getText(R.string.pay_now))) {
                dialogPay()
            }

        }
        subscriptionPlan()
    }

    private fun dialogPay() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_payment_layout, null)
        dialogBuilder.setView(dialogView)
        dialogView.btnVoucher.setOnClickListener {
            if (subscriptionRequest.planId.isNotBlank()) {
                val intent = Intent(this@SubscriptionPlanActivity, VoucherActivity::class.java)
                intent.putExtra(SubscriptionRequest::class.java.name, subscriptionRequest)
                startActivityForResult(intent, 123)
                alertDialog?.dismiss()
            } else {
                toast(R.string.message_select_subscription_plan)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VOUCHER && resultCode == Activity.RESULT_OK && data != null) {
            val voucherResult = data.getSerializableExtra(VoucherResult::class.java.name) as VoucherResult
            subscriptionRequest.planDuration = voucherResult.expireAfter
            subscriptionRequest.transactionId = voucherResult.code
            subscriptionRequest.transactionType = VOUCHER
            updateSubscription()
        } else if (requestCode == REQUEST_CODE_PAYTABS) {
            val sharedPrefs = getSharedPreferences("paytabs_shared", MODE_PRIVATE)
            val responseCode = sharedPrefs.getString("pt_response_code", "")
            val transactionId = sharedPrefs.getString("pt_transaction_id", "")
            toast("PayTabs Response Code : " + responseCode)
            toast("Paytabs transaction ID after payment : " + transactionId)
            subscriptionRequest.planDuration = 0
            subscriptionRequest.transactionId = transactionId
            subscriptionRequest.transactionType = PAY
            updateSubscription()
        }
    }


    private fun subscriptionPlan() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.subscriptionPlan(Prefs(this).deviceId, Prefs(this).securityToken, subscriptionRequest)
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

    private fun updateSubscription() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.updateSubscription(Prefs(this).deviceId, Prefs(this).securityToken, subscriptionRequest)
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
                if (response.errorCode == 200) {
                    llParent.visible()
                    if (response.obj.isJsonArray) {
                        val bean = Gson().fromJson<ArrayList<SubscriptionPlanResult>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<SubscriptionPlanResult>>() {}.type)
                        planList.clear()
                        planList.addAll(bean)
                        if (tvPayNow.text.equals(resources.getText(R.string.str_renew))) {

                        } else if (tvPayNow.text.equals(resources.getText(R.string.pay_now))) {
                            subscriptionPlanAdapter.addList(planList)
                        }

                    }
                    if (response.obj.isJsonObject) {
                        toast(response.message)
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
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

//    private fun payTabs() {
//        val intent = Intent(this@SubscriptionPlanActivity, PayTabActivity::class.java)
//        intent.putExtra("pt_merchant_email", "mohannad@konozstore.com") //this a demo account for testing the sdk
//        intent.putExtra("pt_secret_key", "mHdyDzGHkGyFM7PrJj29ogjtY8FuqKvNsIm5i2NDJmivt8Xcr0lQgEkeNZIeJe1cWoifq5S9raFViD8KDuBT8V8WVRbF5cR1B9h1")//Add your Secret Key Here
//        intent.putExtra("pt_transaction_title", "Subscription")
//        intent.putExtra("pt_amount", subscriptionPlanResult.price)
//        intent.putExtra("pt_currency_code", "SAR") //Use Standard 3 character ISO
//        intent.putExtra("pt_shared_prefs_name", "paytabs_shared")
//        intent.putExtra("pt_customer_email", "mohannad@konozstore.com")
//        intent.putExtra("pt_customer_phone_number", "00966554055502")
//        intent.putExtra("pt_order_id", "1234567")
//        intent.putExtra("pt_product_name", subscriptionPlanResult.name)
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

    fun selectPlan() {

        /*planList.sortWith(compareBy ({
            it.name
        }))*/
        Collections.sort(planList,
                { o1, o2 -> o1.name.compareTo(o2.name) })

        var list: ArrayList<String> = ArrayList()
        for (i in 0 until planList.size) {
            list.add(planList.get(i).name)
        }
        val licenseTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)

        val licenseTypeBuilder = android.app.AlertDialog.Builder(this)
                .setSingleChoiceItems(licenseTypeAdapter, 0) { dialog, which ->
                    subscriptionPlanResult = planList.get(which)
                    subscriptionRequest.planId = planList.get(which).id

                    dialogPay()
                    dialog.cancel()
                }
        val licenseTypeAlert = licenseTypeBuilder.create()
        licenseTypeAlert.show()
    }

}
