package com.gropse.serviceme.fragment.provider

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.HomeProviderActivity
import com.gropse.serviceme.activities.provider.OrderProviderActivity
import com.gropse.serviceme.activities.both.SubscriptionPlanActivity
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.slider.BaseSliderView
import com.gropse.serviceme.slider.DefaultSliderView
import com.gropse.serviceme.slider.SliderLayout
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home_provider.*

class HomeProviderFragment : BaseFragment() {
    private var bean = HomeResult()
    private var commonRequest = CommonRequest()

    companion object {
        fun newInstance(): HomeProviderFragment {
            return HomeProviderFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_provider, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeProviderActivity).setUpToolbar(R.string.home, false)

        tvReady.setOnClickListener {
            ready()
            readyNotReadyProvider()
        }

        tvNotReady.setOnClickListener {
            notReady()
            readyNotReadyProvider()
        }

        llRequest.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.SERVICE_REQUEST)
            intent.putExtra("count", bean.requests)
            startActivity(intent)
        }

        llScheduled.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.SCHEDULED_ORDERS)
            intent.putExtra("count", bean.scheduled)
            startActivity(intent)
        }

        llCompleted.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.COMPLETED_ORDERS)
            intent.putExtra("count", bean.completed)
            startActivity(intent)
        }

        llOngoing.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.ONGOING_ORDERS)
            intent.putExtra("count", bean.ongoing)
            startActivity(intent)
        }

        llCancelled.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.CANCELLED_ORDERS)
            intent.putExtra("count", bean.cancelled)
            startActivity(intent)
        }

        llSubscription.setOnClickListener {
            val intent = Intent(activity, SubscriptionPlanActivity::class.java)
            if (bean.plan?.planDuration != 0L)
                intent.putExtra(PlanResult::class.java.name, bean.plan)
            startActivityForResult(intent, 111)
        }

        llMissing.setOnClickListener {
            val intent = Intent(activity, OrderProviderActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.MISSING_ORDERS)
            intent.putExtra("count", bean.missing)
            startActivity(intent)
        }

        commonRequest.userId = Prefs(activity).userId
        commonRequest.providerType = Prefs(activity).userType

        homeProvider()

        initSlider()

    }

    private fun noStatus() {
        tvReady.circularLeftSideBorderGradient()
        tvNotReady.circularRightSideBorderGradient()
        tvReady.textColor(R.color.colorPrimary)
        tvNotReady.textColor(R.color.colorPrimary)
    }

    private fun ready() {
        tvReady.circularLeftSideGradient()
        tvNotReady.circularRightSideBorderGradient()
        tvReady.textColor(R.color.colorWhite)
        tvNotReady.textColor(R.color.colorPrimary)
    }

    private fun notReady() {
        tvReady.circularLeftSideBorderGradient()
        tvNotReady.circularRightSideGradient()
        tvReady.textColor(R.color.colorPrimary)
        tvNotReady.textColor(R.color.colorWhite)
    }

    private fun updateUI() {
        tvRequest.text = String.format("%s (%d)", getString(R.string.service_request), bean.requests)
        tvSubscription.text = getString(R.string.subscription)
        tvScheduled.text = String.format("%s (%d)", getString(R.string.scheduled_order), bean.scheduled)
        tvCompleted.text = String.format("%s (%d)", getString(R.string.completed_order), bean.completed)
        tvOnGoing.text = String.format("%s (%d)", getString(R.string.ongoing_orders), bean.ongoing)
        tvCancelled.text = String.format("%s (%d)", getString(R.string.cancelled_orders), bean.cancelled)
        tvMissing.text = String.format("%s (%d)", getString(R.string.missing_orders), bean.missing)
        if (bean.readyStatus == 1) {
            ready()
        } else {
            notReady()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            homeProvider()
        }
    }

    private fun initSlider() {
        val urlList = ArrayList<String>()
        urlList.add("http://allwashes.com/service_me/img/1.png")
        urlList.add("http://allwashes.com/service_me/img/2.png")

        for (url in urlList) {
            val textSliderView = DefaultSliderView(activity)
            textSliderView.image(url).scaleType = BaseSliderView.ScaleType.Fit
            textSliderView.bundle(Bundle())
            slider.addSlider(textSliderView)
        }
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        slider.setDuration(4000)
    }

    private fun homeProvider() {
        if (activity.isNetworkAvailable()) {
            if (activity != null) activity?.runOnUiThread { mProgressDialog?.show() }
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.homeProvider(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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

    private fun readyNotReadyProvider() {
        if (activity.isNetworkAvailable()) {
            if (activity != null) activity?.runOnUiThread { mProgressDialog?.show() }
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.readyNotReadyProvider(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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
            if (response is HomeResponse) {
                if (response.errorCode == 200) {
                    bean = response.result ?: HomeResult()
                    Prefs(activity).transactionId = bean!!.plan!!.transactionId
                    updateUI()
                }
            } else if (response is ReadyNotReadyResponse) {
                if (response.errorCode != 200) {
                    if (response.result?.readyStatus == 1) {
                        ready()
                    } else {
                        notReady()
                    }
                }
            }
            if (activity != null)
                activity?.runOnUiThread {
                    mProgressDialog?.hide()
                }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        if (activity != null)
            activity?.runOnUiThread {
                mProgressDialog?.hide()
                activity.toast(R.string.message_error_connection)
            }
    }


}