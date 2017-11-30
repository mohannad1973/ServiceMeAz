package com.gropse.serviceme.activities.user

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.adapter.ProviderListAdapter
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_provider_list.*
import kotlinx.android.synthetic.main.dialog_request_sent.view.*

class ProviderListActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var providerListAdapter: ProviderListAdapter
    private var mActivity: Activity? = null
    private var dialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    private var addServiceResult = AddServiceResult()
    private var commonRequest = CommonRequest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_list)
        setUpToolbar(R.string.service_providers)
        mActivity = this

        val mapFragment = SupportMapFragment.newInstance(GoogleMapOptions().zOrderOnTop(true))
        supportFragmentManager.beginTransaction().replace(R.id.flMap, mapFragment).commit()
        mapFragment.getMapAsync(this)


        addServiceResult = intent.getSerializableExtra(AddServiceResult::class.java.name) as AddServiceResult
        commonRequest.serId = addServiceResult.serId

        rvProviderList.layoutManager = LinearLayoutManager(mActivity)
        providerListAdapter = ProviderListAdapter(object : ProviderListAdapter.OnItemClick {
            override fun onClick(bean: Providers, type: Int) {
                commonRequest.providerId = bean.proId
                commonRequest.providerType = bean.providerType
                requestService()
            }
        })
        rvProviderList.adapter = providerListAdapter
        providerListAdapter.addList(addServiceResult.result ?: ArrayList())
    }

    override fun onMapReady(map: GoogleMap?) {
        map.let {
            addServiceResult.result?.forEach {
                if (it.latitude.isNotBlank() && it.longitude.isNotBlank()) {
                    val latLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                    map?.addMarker(MarkerOptions().position(latLng))
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                }
            }
        }
    }

    private fun dialogRequestSent() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_request_sent, null)
        dialogBuilder?.setView(dialogView)
        dialogView.btnDone.setOnClickListener {
            alertDialog?.dismiss()
            val brodCastIntent = Intent()
            brodCastIntent.action = AppConstants.FINISH
            sendBroadcast(brodCastIntent)
            finish()
        }
        alertDialog = dialogBuilder?.create()
        if (alertDialog?.window != null) {
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    private fun requestService() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            frameLayout.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.requestService(Prefs(this).deviceId, Prefs(this).securityToken, commonRequest)
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
                    dialogRequestSent()
                }
            }
            progressBar.gone()
            frameLayout.gone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        progressBar.gone()
        frameLayout.gone()
        toast(R.string.message_error_connection)

    }
}
