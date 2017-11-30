package com.gropse.serviceme.fragment.user

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_current_service_user.*
import net.ralphpina.permissionsmanager.PermissionsManager

class CurrentServiceUserFragment : BaseFragment() {

    internal var mActivity: Activity? = null
    private var commonRequest = CommonRequest()
    private var isFirstCall = true

    companion object {
        fun newInstance(): CurrentServiceUserFragment {
            return CurrentServiceUserFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_current_service_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeUserActivity).setUpToolbar(R.string.current_service, false)

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
                                        currentServiceUser()
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
        tvPhoneNumber.text = bean.phone
        tvStatus.text = when (bean.status) {
            1 -> getString(R.string.accepted)
            2 -> getString(R.string.arriving)
            3 -> getString(R.string.reached)
            4 -> getString(R.string.started)
            5 -> getString(R.string.completed)
            else -> getString(R.string.accepted)
        }
    }

    private fun currentServiceUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.currentServiceUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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
                    1 -> activity.logout()
                    3 -> {
                        tvNoService.visible()
                        activity.toast(response.message)
                    }
                    200 -> {
                        if (response.obj.isJsonObject) {
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