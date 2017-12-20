package com.gropse.serviceme.activities.provider

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.MyApplication
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.activities.both.ReviewRatingActivity
import com.gropse.serviceme.adapter.OrderProviderAdapter
import com.gropse.serviceme.custom.CustomLinearLayoutManager
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
import kotlinx.android.synthetic.main.activity_orders.*
import java.util.*

class OrderProviderActivity : BaseActivity() {

    private var commonRequest = CommonRequest()
    private lateinit var orderProviderAdapter: OrderProviderAdapter
    private var isFirstCall = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)


        commonRequest.userId = Prefs(this).userId
        commonRequest.providerType = Prefs(this).userType
        commonRequest.latitude = Prefs(this).latitude
        commonRequest.longitude = Prefs(this).longitude

        if (intent.hasExtra(AppConstants.SCREEN)) {
            val type = intent.getStringExtra(AppConstants.SCREEN)
            val count = intent?.getIntExtra("count", 0)
            var resId = R.string.service_request

            recyclerView.layoutManager = CustomLinearLayoutManager(this)
            recyclerView.setOnClickListener{

            }
            orderProviderAdapter = OrderProviderAdapter(type,this, object : OrderProviderAdapter.OnItemClick {
                override fun onClick(bean: OrderResult, action: Int) {
                    when (action) {
                        OrderProviderAdapter.NONE -> {
                            when (type) {
                                AppConstants.SERVICE_REQUEST -> {
//                            if (bean.timeLeft != -1L) {
                                    val intent = Intent(this@OrderProviderActivity, AcceptingActivity::class.java)
                                    intent.putExtra(OrderResult::class.java.name, bean)
                                    startActivity(intent)
//                                }
                                }
                                AppConstants.SCHEDULED_ORDERS -> {
                                    val intent = Intent(this@OrderProviderActivity, AcceptingActivity::class.java)
                                    intent.putExtra(OrderResult::class.java.name, bean)
                                    startActivity(intent)
                                }
                                AppConstants.COMPLETED_ORDERS -> {

                                }
                                AppConstants.CANCELLED_ORDERS -> {

                                }
                                AppConstants.ONGOING_ORDERS -> {
                                    val intent = Intent(this@OrderProviderActivity, CurrentServiceProviderActivity::class.java)
                                    intent.putExtra(OrderResult::class.java.name, bean)
                                    startActivity(intent)
                                }
                            }
                        }
                        OrderProviderAdapter.ACCEPT -> {
                            //if (bean.timeLeft != -1L) {
                                commonRequest.serId = bean.serId
                            commonRequest.transactionId = "123456789"
                                acceptProvider()
                            //}
                        }
                        OrderProviderAdapter.REJECT -> {
//                            if (bean.timeLeft != -1L) {
                                commonRequest.serId = bean.serId
                                rejectProvider()
//                            }
                        }
                        OrderProviderAdapter.FEEDBACK -> {
                            val intent = Intent(this@OrderProviderActivity, ReviewRatingActivity::class.java)
                            intent.putExtra(OrderResult::class.java.name, bean)
                            intent.putExtra(AppConstants.SCREEN, AppConstants.USER)
                            startActivity(intent)
                        }
                    }
                }
            })
            recyclerView.adapter = orderProviderAdapter

            when (type) {
                AppConstants.SERVICE_REQUEST -> {
                    resId = R.string.service_request
                    if (Prefs(this).latitude.isNotBlank() && Prefs(this).longitude.isNotBlank()) {
                        commonRequest.latitude = Prefs(this).latitude
                        commonRequest.longitude = Prefs(this).longitude
                        requestsProvider()
                    }
//                    } else {
//                        progressBar.visible()
//                        (application as MyApplication).checkLocationPermission()
//                    PermissionsManager.get()
//                            .requestLocationPermission()
//                            .subscribe { permissionsResult ->
//                                if (permissionsResult.isGranted) { // always true pre-M
//                                    startLocationRequest()
//                                }
//                                if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
//                                    // do whatever
//                                }
//                            }
                }
                AppConstants.SCHEDULED_ORDERS -> {
                    resId = R.string.scheduled_order
                    scheduledProvider()
                }
                AppConstants.COMPLETED_ORDERS -> {
                    resId = R.string.completed_order
                    completedProvider()
                }
                AppConstants.CANCELLED_ORDERS -> {
                    resId = R.string.cancelled_orders
                    cancelledProvider()
                }
                AppConstants.ONGOING_ORDERS -> {
                    resId = R.string.ongoing_orders
                    ongoingProvider()
                }

                AppConstants.MISSING_ORDERS -> {
                    resId = R.string.missing_orders
                    missingProvider()
                }

            }
            setUpToolbar(resId)
            tvTotal.text = String.format("%s %s (%d)", getString(R.string.total), getString(resId), count)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {
        val rxLocation = RxLocation(this)

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
                                        requestsProvider()
                                    }
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }
    }


    private fun requestsProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val request = commonRequest.toString()
            val disposable = client.requestsProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun scheduledProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.scheduledProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun completedProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.completedProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun cancelledProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.cancelledProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun ongoingProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.ongoingProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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

    private fun missingProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.missingProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
                        requestsProvider()
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun rejectProvider() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.rejectProvider(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
                    if (response.obj.isJsonArray) {
                        rlParent.visible()
                        tvNoService.gone()
                        val bean = Gson().fromJson<ArrayList<OrderResult>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<OrderResult>>() {}.type)
                        orderProviderAdapter.clear()
                        orderProviderAdapter.addList(bean)
                    }
                    if (response.obj.isJsonObject){
                        toast(response.message)
                    }
                } else {
                    rlParent.gone()
                    tvNoService.visible()
                }
            }
//            if (response is OrderResponse) {
//                if (response.errorCode == 200) {
//                    val bean = response.result ?: ArrayList()
//                    orderProviderAdapter.clear()
//                    orderProviderAdapter.addList(bean)
//                }
//            }
//            if (response is BaseObjectResponse) {
//                toast(response.message)
//            }
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