package com.gropse.serviceme.activities.terms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.activities.provider.ViewProblemProviderActivity
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.AppConstants

class TermsActivity : BaseActivity(), OnMapReadyCallback {

    private var orderResult = OrderResult()
    private val receiverFinish = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        setUpToolbar(R.string.accepting_result)

        val mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().zOrderOnTop(true))
        supportFragmentManager.beginTransaction().replace(R.id.flMap, mapFragment).commit()
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(map: GoogleMap?) {
        map.let {
            if (orderResult.latitude.isNotBlank() && orderResult.longitude.isNotBlank()) {
                val latLng = LatLng(orderResult.latitude.toDouble(), orderResult.longitude.toDouble())
                it?.addMarker(MarkerOptions().position(latLng))
                it?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
            }
        }
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        unregisterReceiver(receiverFinish)
    }

}