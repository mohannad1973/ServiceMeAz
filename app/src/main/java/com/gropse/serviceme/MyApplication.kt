package com.gropse.serviceme

import android.annotation.SuppressLint
import android.location.Address
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.gropse.serviceme.utils.Prefs
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.patloew.rxlocation.RxLocation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import net.ralphpina.permissionsmanager.PermissionsManager
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.view.ContextThemeWrapper
import android.widget.Toast
import com.gropse.serviceme.utils.ContextWrapper
import com.gropse.serviceme.utils.LocaleUtils
import java.util.*
import io.reactivex.internal.operators.single.SingleInternalHelper.toObservable
import com.gropse.serviceme.R.string.location
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.util.NotificationLite.disposable
import io.reactivex.internal.operators.single.SingleInternalHelper.toObservable
import com.gropse.serviceme.R.string.location




class MyApplication : MultiDexApplication() {

    var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    var mCurrentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        PermissionsManager.init(this)
        RxPaparazzo.register(this)
                .withFileProviderAuthority("")
                .withFileProviderPath("")
        checkLocationPermission()
        if (Prefs(this).locale.equals("")) {
            Prefs(this).locale = "en"
        }
    }


    public fun getCurrentActivity(): Activity?{
        return mCurrentActivity
    }

    private val lifeCycle = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            mCurrentActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }
    }

    fun checkLocationPermission(){
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

        val rxLocation = RxLocation(this)

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

        compositeDisposable?.add(rxLocation.settings().checkAndHandleResolution(locationRequest)
                .subscribe { aBoolean ->
                    if (aBoolean) {

                        compositeDisposable?.add(rxLocation.location()
                                .updates(locationRequest)
                                .flatMap<Address> { location -> rxLocation.geocoding().fromLocation(location).toObservable() }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { address ->
                                    Log.d("Address",""+address)
                                    Prefs(this).latitude = address.latitude.toString()
                                    Prefs(this).longitude = address.longitude.toString()
                                    Prefs(this).location = address.locality
                                    Prefs(this).countryName = address.countryName
                                    Prefs(this).countryCode = when (address.countryName.toLowerCase()) {
                                        "US".toLowerCase() -> "+1"
                                        "Canada".toLowerCase() -> "+1"
                                        "India".toLowerCase() -> "+91"
                                        "Pakistan".toLowerCase() -> "+92"
                                        "Cyprus".toLowerCase() -> "+537"
                                        "Bahrain".toLowerCase() -> "+973"
                                        "Egypt".toLowerCase() -> "+20"
                                        "Iran".toLowerCase() -> "+98"
                                        "Iraq".toLowerCase() -> "+964"
                                        "Israel".toLowerCase() -> "+972"
                                        "Jordan".toLowerCase() -> "+962"
                                        "Kuwait".toLowerCase() -> "+965"
                                        "Lebanon".toLowerCase() -> "+961"
                                        "Oman".toLowerCase() -> "+968"
                                        "Palestine".toLowerCase() -> "+970"
                                        "Qatar".toLowerCase() -> "+974"
                                        "Saudi Arabia".toLowerCase() -> "+966"
                                        "Syria".toLowerCase() -> "+963"
                                        "Turkey".toLowerCase() -> "+90"
                                        "United Arab Emirates".toLowerCase() -> "+971"
                                        "Yemen".toLowerCase() -> "+967"
                                        "Bangladesh".toLowerCase() -> "+880"
                                        else -> ""
                                    }
                                    compositeDisposable?.dispose()
                                })
                    }
                })

        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }

    }
}