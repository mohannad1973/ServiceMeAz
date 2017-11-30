package com.gropse.serviceme.fragment.provider

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.HomeProviderActivity
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_status_layout.view.*
import kotlinx.android.synthetic.main.fragment_current_service_provider.*
import net.ralphpina.permissionsmanager.PermissionsManager

class CurrentServiceProviderFragment : BaseFragment() {

    private var dialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    internal var mActivity: Activity? = null
    private var commonRequest = CommonRequest()
    private var isFirstCall = true

    companion object {
        fun newInstance(): CurrentServiceProviderFragment {
            return CurrentServiceProviderFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_current_service_provider, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeProviderActivity).setUpToolbar(R.string.current_service, false)

        commonRequest.userId = Prefs(activity).userId
        commonRequest.providerType = Prefs(activity).userType

        PermissionsManager.get()
                .requestLocationPermission()
                .subscribe { permissionsResult ->
                    if (permissionsResult.isGranted) { // always true pre-M
                        startLocationRequest()
                    }
                    if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
                        // do whatever
                    }
                }

        tvStatus.setOnClickListener {
            dialog()
        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {
        val rxLocation = RxLocation(activity)

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

        compositeDisposable?.add(rxLocation.settings().checkAndHandleResolution(locationRequest)
                .subscribe { aBoolean ->
                    if (aBoolean) {
                        compositeDisposable?.add(rxLocation.location().updates(locationRequest)
                                .flatMap<Address> { location -> rxLocation.geocoding().fromLocation(location).toObservable() }
                                .subscribe { address ->
                                    commonRequest.latitude = address.latitude.toString()
                                    commonRequest.longitude = address.longitude.toString()
                                    if (commonRequest.latitude.isNotBlank() && commonRequest.longitude.isNotBlank() && isFirstCall) {
                                        isFirstCall = false
                                        currentServiceProvider()
                                    }
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }
    }

    private fun updateUI(bean: OrderResult) {
        ivProvider.loadUrl(bean.image)
        tvName.text = bean.name
        tvAddress.text = bean.address
        tvDistance.roundDecimal(bean.distance)
        tvServiceType.text = bean.serType
        tvLocation.text = bean.location
        tvPhoneNumber.text = bean.phone
        changeStatus(bean.status)
    }

    private fun changeStatus(status: Int) {
        tvStatus.text = when (status) {
            1 -> getString(R.string.accepted)
            2 -> getString(R.string.arriving)
            3 -> getString(R.string.reached)
            4 -> getString(R.string.started)
            5 -> getString(R.string.completed)
            else -> getString(R.string.accepted)
        }
        commonRequest.status = status
    }

    private fun dialog() {
        dialogBuilder = AlertDialog.Builder(activity)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.dialog_status_layout, null)
        dialogBuilder?.setView(view)

        view.tvAccepted.setOnClickListener {
            view.ivStatus.setImageResource(R.drawable.status2)
            changeStatus(1)
        }
        view.tvArriving.setOnClickListener {
            view.ivStatus.setImageResource(R.drawable.status3)
            changeStatus(2)
        }
        view.tvReached.setOnClickListener {
            view.ivStatus.setImageResource(R.drawable.status4)
            changeStatus(3)
        }
        view.tvStarted.setOnClickListener {
            view.ivStatus.setImageResource(R.drawable.status5)
            changeStatus(4)
        }
        view.tvCompleted.setOnClickListener {
            view.ivStatus.setImageResource(R.drawable.status6)
            changeStatus(5)
        }

        view.btnDone.setOnClickListener {
            alertDialog?.dismiss()
            statusProvider()
        }

        view.post {
            when (commonRequest.status) {
                1 -> view.tvAccepted.performClick()
                2 -> view.tvArriving.performClick()
                3 -> view.tvReached.performClick()
                4 -> view.tvStarted.performClick()
                5 -> view.tvCompleted.performClick()
            }
        }

        alertDialog = dialogBuilder?.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    private fun statusProvider() {
        if (activity.isNetworkAvailable()) {
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.statusProvider(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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

    private fun currentServiceProvider() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.currentServiceProvider(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun onResponse(response: Any, resType: String = "") {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> activity.logout()
                    3 -> {
                        if (resType == OrderResult::class.java.name) {
                            tvNoService.visible()
                        } else {
                            changeStatus(commonRequest.status - 1)
                        }
                        activity.toast(response.message)
                    }
                    200 -> {
                        if (response.obj.isJsonObject && resType == OrderResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), OrderResult::class.java)
                            llParent.visible()
                            updateUI(bean ?: OrderResult())
                        }
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
        tvNoService.visible()
        activity.toast(R.string.message_error_connection)
    }

}