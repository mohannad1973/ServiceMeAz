package com.gropse.serviceme.activities.both

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import com.google.android.gms.location.LocationRequest
import com.gropse.serviceme.MyApplication
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.SignUpProviderActivity
import com.gropse.serviceme.activities.user.SignUpUserActivity
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_option.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class OptionActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    var isOptionShown = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        llLocation.circularDrawable(R.color.colorButton)
        tvSignUpUser.circularDrawable()
        tvSignUpProvider.circularDrawable()
        tvSkip.circularDrawable()
        ivPin.circularDrawable(R.color.colorWhite)
        llChangeLanguage.circularDrawable(R.color.colorButton)

        tvLocation.setText(Prefs(this).countryName)


        llLocation.setOnClickListener {
            startLocationRequest()
            Log.d("Location","Country:" +Prefs(this).countryName)
        }

        llChangeLanguage.setOnClickListener{
            if (!isOptionShown) {
                llOptions.visibility = View.VISIBLE
                isOptionShown = true
            }
            else {
                llOptions.visibility = View.INVISIBLE
                isOptionShown = false
            }
        }

//        llLanguage1.circularBorderDrawable(5)
//        llLanguage2.circularBorderDrawable(5)

        tvLogin.spannableText(getString(R.string.already_have_an_account_login, getString(R.string.login)), getString(R.string.login))

        Prefs(this).deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        tvEnglish.setOnClickListener {
            english()
            recreate()
        }
        tvArabic.setOnClickListener {
            arabic()
            recreate()
        }
        tvUrdu.setOnClickListener {
            urdu()
            recreate()
        }
        tvTurkish.setOnClickListener{
            turkish()
            recreate()
        }
        tvRussian.setOnClickListener {
            russian()
            recreate()
        }
        tvSignUpUser.setOnClickListener({ startActivity(Intent(this, SignUpUserActivity::class.java)) })
        tvSignUpProvider.setOnClickListener({ startActivity(Intent(this, SignUpProviderActivity::class.java)) })
        tvLogin.setOnClickListener({ startActivity(Intent(this, LoginActivity::class.java)) })
//        tvSkip.setOnClickListener({ startActivity(Intent(this, OptionActivity::class.java)) })


        when (Prefs(this).locale) {
            "en" -> english()
            "ar" -> arabic()
            "ur" -> urdu()
            "tr" -> turkish()
            "ru" -> russian()
            else -> english()
        }
        methodRequiresTwoPermission()
    }

    private fun resetViews(){
        isOptionShown = false
        llOptions.visibility = View.INVISIBLE
        tvEnglish.isSelected = false
        tvArabic.isSelected = false
        tvTurkish.isSelected = false
        tvUrdu.isSelected = false
        tvRussian.isSelected = false


        //tvEnglish.circularBorderDrawable(5)
        //tvArabic.circularBorderDrawable(5)
        //tvUrdu.circularBorderDrawable(5)

        tvEnglish.textColor(R.color.colorGrey)
        tvArabic.textColor(R.color.colorGrey)
        tvUrdu.textColor(R.color.colorGrey)
        tvTurkish.textColor(R.color.colorGrey)
        tvRussian.textColor(R.color.colorGrey)

    }

    private fun english(){
        resetViews()
        tvEnglish.isSelected = true
        tvEnglish.textColor(R.color.colorWhite)
        Prefs(this).locale = "en"
    }

    private fun turkish(){
        resetViews()
        tvTurkish.isSelected = true
        tvTurkish.textColor(R.color.colorWhite)
        Prefs(this).locale = "tr"
    }

    private fun russian(){
        resetViews()
        tvRussian.isSelected = true
        tvRussian.textColor(R.color.colorWhite)
        Prefs(this).locale = "ru"
    }



    private fun arabic(){
        resetViews()
        //tvArabic.circularDrawable()
        tvArabic.isSelected = true
        tvArabic.textColor(R.color.colorWhite)
        Prefs(this).locale = "ar"
    }

    private fun urdu(){
        resetViews()
        tvUrdu.isSelected = true
        tvUrdu.textColor(R.color.colorWhite)
        Prefs(this).locale = "ur"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(AppConstants.RC_ALL_NECESSARY_PERMISSION)
    private fun methodRequiresTwoPermission() {
        val perms = AppConstants.PERMISSION_ALL_NECESSARY
        if (EasyPermissions.hasPermissions(this, *perms)) {
            startLocationRequest()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera_storage_location), AppConstants.RC_ALL_NECESSARY_PERMISSION, *perms)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        startLocationRequest()
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {

    }

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {


        mFusedLocationClient!!.getLastLocation()
                .addOnSuccessListener(this) { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        Log.d("location",""+location.latitude+" "+location.longitude)
                        //tvLocation.setText()
                    }
                }
                .addOnFailureListener(this){
                    Log.d("fail","fail")
                }


        val rxLocation = RxLocation(this);

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

        compositeDisposable?.add(rxLocation.settings().checkAndHandleResolution(locationRequest)
                .subscribe { aBoolean ->
                    if (aBoolean) {
                      /*  compositeDisposable?.add(rxLocation.location().updates(locationRequest)
                                .flatMap<Address> { location -> rxLocation.geocoding().fromLocation(location).toObservable() }
                                .subscribe { address ->
                                    tvLocation.text = address.countryName
                                })*/

                        compositeDisposable?.add(rxLocation.location().lastLocation()
                                .flatMapObservable { location -> rxLocation.geocoding().fromLocation(location).toObservable() }
                                .subscribe { address ->
                                    /* do something */
                                    tvLocation.text = address.countryName
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler({ it.printStackTrace() })
    }
}
