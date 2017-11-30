package com.gropse.serviceme.activities.provider

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
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import kotlinx.android.synthetic.main.activity_accepting.*

class AcceptingActivity : BaseActivity(), OnMapReadyCallback {

    private var orderResult = OrderResult()
    private val receiverFinish = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accepting)
        setUpToolbar(R.string.accepting_result)

        btnGetDirection.circularDrawable()
        btnViewProblem.circularDrawable()

        val mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().zOrderOnTop(true))
        supportFragmentManager.beginTransaction().replace(R.id.flMap, mapFragment).commit()
        mapFragment.getMapAsync(this)

        orderResult = intent.getSerializableExtra(OrderResult::class.java.name) as OrderResult
        ivImage.loadUrl(orderResult.image)
        tvName.text = orderResult.name
        tvServiceType.text = orderResult.serType
        tvLocation.text = orderResult.location
        tvDistance.roundDecimal(orderResult.distance)

        btnGetDirection.setOnClickListener {
            if (orderResult.latitude.isNotBlank() && orderResult.longitude.isNotBlank()) {
                val intentA = Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + orderResult.latitude + "," + orderResult.longitude + "?q=" + orderResult.latitude + "," + orderResult.longitude + "(" + orderResult.name + ")"))
                if (isAppInstalled(intentA, true))
                    startActivity(intentA)
            }
        }

        btnViewProblem.setOnClickListener {
            val intent = Intent(this@AcceptingActivity, ViewProblemProviderActivity::class.java)
            intent.putExtra(OrderResult::class.java.name, orderResult)
            startActivity(intent)
        }

        val filter = IntentFilter()
        filter.addAction(AppConstants.FINISH)
        registerReceiver(receiverFinish, filter)
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
