package com.gropse.serviceme.activities.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ScrollView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_request.*
import kotlinx.android.synthetic.main.dialog_confirmation_layout.view.*
import kotlinx.android.synthetic.main.dialog_request_sent.view.*
import net.ralphpina.permissionsmanager.PermissionsManager
import java.util.*


class CreateRequestActivity : BaseActivity(), OnMapReadyCallback {

    var map: GoogleMap? = null
//    private var bean = CategoryResult()
    private var dialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    private var addServiceRequest = AddServiceRequest()
    private var isAutoLocation = true
    private var date = ""
    private var time = ""
    private val receiverFinish = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        setUpToolbar(R.string.new_request)

        if (intent.hasExtra(CategoryResult::class.java.name)) {
            val bean = intent.getSerializableExtra(CategoryResult::class.java.name) as CategoryResult

            tvService.text = bean.name
            addServiceRequest.serName = bean.name
            addServiceRequest.serCatId = bean.catId
        }

        ivPin.loadUrl("http://maps.google.com/mapfiles/ms/icons/red-dot.png")
        tvService.circularBorderDrawable(5)
        tvDate.circularBorderDrawable(5)
        tvTime.circularBorderDrawable(5)
        tvDate.textColor(R.color.colorPrimary)
        tvTime.textColor(R.color.colorPrimary)
        etDescription.circularBorderDrawable(5, R.dimen._40sdp)
        rlSearchLocation.circularBorderDrawable(5)

        btnAddPhoto.circularDrawable()
        btnAddVideo.circularDrawable()
        btnCancel.circularDrawable()
        btnBookNow.circularDrawable()
        btnSchedule.circularDrawable()
        btnConfirm.circularDrawable()

        btnBookNow.setOnClickListener {
            addServiceRequest.isScheduled = "0"
            if (isValid()) dialogAddService()
        }

        btnConfirm.setOnClickListener {
            addServiceRequest.isScheduled = "1"
            if (isValid()) dialogAddService()
        }

        btnSchedule.setOnClickListener {
            if (llSchedule.visibility == 0) {
                btnBookNow.visible()
                llSchedule.gone()
            } else {
                btnBookNow.gone()
                llSchedule.visible()
                scrollView.post {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }

        btnAddPhoto.setOnClickListener {
            val intent = Intent(this@CreateRequestActivity, VideoImageActivity::class.java)
            //intent.putExtra(ImageUtility::class.java.name, imageBean) as ImageUtility.ImageBean
            startActivityForResult(intent, 909)
        }

        btnAddVideo.setOnClickListener {
            val intent = Intent(this@CreateRequestActivity, VideoImageActivity::class.java)
            //intent.putExtra(ImageUtility::class.java.name, imageBean) as ImageUtility.ImageBean
            startActivityForResult(intent, 909)
        }

        tvDate.setOnClickListener {
            hideKeyboard()
            val c = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, myDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            val cal = Calendar.getInstance()
            cal.time = Date()
            datePickerDialog.datePicker.minDate = cal.timeInMillis
            log("date", cal.time.toString() + "")
            datePickerDialog.show()
        }

        tvTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                run {
                    time = selectedHour.toString() + ":" + selectedMinute + ":00"
                    tvTime.text = time
                }
            }, hour, minute, true)//Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        val mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().zOrderOnTop(true))
        supportFragmentManager.beginTransaction().replace(R.id.flMap, mapFragment).commit()
        mapFragment.getMapAsync(this)

        val acfSearchLocation = fragmentManager.findFragmentById(R.id.acfSearchLocation) as PlaceAutocompleteFragment
        acfSearchLocation.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status?) {

            }

            override fun onPlaceSelected(place: Place) {
                addServiceRequest.latitude = place.latLng.latitude.toString()
                addServiceRequest.longitude = place.latLng.longitude.toString()
                addServiceRequest.location = place.name.toString()
                isAutoLocation = false
                map.let {
                    it?.clear()
//                    it?.addMarker(MarkerOptions().position(place.latLng))
                    it?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12F))
                }
            }
        })

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

        val filter = IntentFilter()
        filter.addAction(AppConstants.FINISH)
        registerReceiver(receiverFinish, filter)
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map.let {
            if (Prefs(this).latitude.isNotBlank() && Prefs(this).longitude.isNotBlank()) {
                addServiceRequest.latitude = Prefs(this).latitude
                addServiceRequest.longitude = Prefs(this).longitude
                addServiceRequest.location = Prefs(this).location
                it?.clear()
                it?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Prefs(this).latitude.toDouble(), Prefs(this).longitude.toDouble()), 12F))
            }
        }
        map?.setOnCameraMoveListener {
            addServiceRequest.latitude = map?.cameraPosition?.target?.latitude.toString()
            addServiceRequest.longitude = map?.cameraPosition?.target?.longitude.toString()
        }
    }

    private val myDateListener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        date = StringBuilder().append(y).append("/").append(String.format(Locale.US, "%02d", m + 1)).append("/").append(String.format(Locale.US, "%02d", d)).toString()
        tvDate.text = date
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 909 && resultCode == Activity.RESULT_OK && data != null) {
            val file = data.getStringExtra("files")
            addServiceRequest.files = if (file.isNotBlank()) file.replace(", $".toRegex(), "") else ""
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
                                    if (isAutoLocation) {
                                        addServiceRequest.latitude = address.latitude.toString()
                                        addServiceRequest.longitude = address.longitude.toString()
                                        addServiceRequest.location = address.locality
                                        map.let {
                                            it?.clear()
                                            val latLng = LatLng(address.latitude, address.longitude)
//                                            it?.addMarker(MarkerOptions().position(latLng))
                                            it?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                                        }
                                    }
                                })
                    }
                })
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("Error", "Error", throwable) }
    }

    private fun isValid(): Boolean {
        addServiceRequest.serDescription = etDescription.string()
        addServiceRequest.userId = Prefs(this).userId


        addServiceRequest.serTime = if (date.isNotBlank() && time.isNotBlank()) formatDateTimeToMillis(date + " " + time) else 0L

        if (addServiceRequest.location.isBlank()) {
            toast(R.string.message_select_location)
            return false
        } else if (addServiceRequest.location.isBlank()) {
            toast(R.string.message_enter_description)
            return false
        } else if (addServiceRequest.isScheduled == "1" && addServiceRequest.serTime == 0L) {
            toast(R.string.message_select_date_time)
        }
        return true
    }

    private fun dialogAddService() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_confirmation_layout, null)
        dialogBuilder?.setView(dialogView)
        dialogView.btnAutomatically.setOnClickListener {
            addServiceRequest.serType = "0"
            addService()
            alertDialog?.dismiss()

        }
        dialogView.btnManually.setOnClickListener {
            addServiceRequest.serType = "1"
            addService()
            alertDialog?.dismiss()
        }

        alertDialog = dialogBuilder?.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    private fun dialogRequestSent() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_request_sent, null)
        dialogBuilder?.setView(dialogView)
        dialogView.btnDone.setOnClickListener {
            alertDialog?.dismiss()
            finish()
        }
        alertDialog = dialogBuilder?.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }


    private fun addService() {
        if (isNetworkAvailable()) {
            this.runOnUiThread { mProgressDialog?.show() }
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.addService(Prefs(this).deviceId, Prefs(this).securityToken, addServiceRequest)
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
                if (response.errorCode == 200) {
                    Prefs(this).clearImage()
                    if (response.obj.isJsonObject) {
                        val bean = Gson().fromJson(response.obj.asJsonObject.toString(), AddServiceResult::class.java)
                        if (bean.result == null) {
                            dialogRequestSent()
                        } else {
                            val intent = Intent(this@CreateRequestActivity, ProviderListActivity::class.java)
                            intent.putExtra(AddServiceResult::class.java.name, bean)
                            startActivity(intent)
                        }
                    }
                }
            }
//            if (response is AddServiceResponse) {
//                if (response.errorCode == 200) {
//                    val bean = response.result ?: AddServiceResult()
//                    val intent = Intent(this@CreateRequestActivity, ProviderListActivity::class.java)
//                    intent.putExtra(AddServiceResult::class.java.name, bean)
//                    startActivity(intent)
//                }
//            }
//            if (response is BaseObjectResponse) {
//                if (response.errorCode == 200) {
//
//                }
//            }
            this.runOnUiThread {
                mProgressDialog?.hide()
            }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        this.runOnUiThread {
            mProgressDialog?.hide()
            toast(R.string.message_error_connection)
        }
    }

    override fun onResume() {
        super.onResume()
        var imageCount = 0
        imageCount += if (Prefs(this).image1.isNotBlank()) 1 else 0
        imageCount += if (Prefs(this).image2.isNotBlank()) 1 else 0
        imageCount += if (Prefs(this).image3.isNotBlank()) 1 else 0
        btnAddPhoto.text = String.format("%s (%d)", getString(R.string.add_photo), imageCount)
        var videoCount = 0
        videoCount += if (Prefs(this).video1.isNotBlank()) 1 else 0
        videoCount += if (Prefs(this).video2.isNotBlank()) 1 else 0
        videoCount += if (Prefs(this).video3.isNotBlank()) 1 else 0
        btnAddVideo.text = String.format("%s (%d)", getString(R.string.add_video), videoCount)
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        unregisterReceiver(receiverFinish)
        Prefs(this).clearImage()
    }
}
