package com.gropse.serviceme.activities.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.adapter.ServiceAdapter
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.CategoryResult
import com.gropse.serviceme.pojo.SignUpRequest
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_service.*
import java.util.*


class ServiceActivity : BaseActivity() {


//    private var serviceList = ArrayList<CategoryResult>()
    private lateinit var serviceAdapter: ServiceAdapter
    private val signUpRequest = SignUpRequest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        if (intent.hasExtra(AppConstants.SERVICE_FOR))
            signUpRequest.type = intent.getStringExtra(AppConstants.SERVICE_FOR)

        setUpToolbar(if (signUpRequest.type==AppConstants.SERVICE_HOME) R.string.home_services else R.string.office_services)

        scrollView.isNestedScrollingEnabled = false

        serviceAdapter = ServiceAdapter(object : ServiceAdapter.OnItemClick {
            override fun onClick(bean: CategoryResult, position: Int) {
                val intent = Intent(this@ServiceActivity, CreateRequestActivity::class.java)
                intent.putExtra(CategoryResult::class.java.name, bean)
                startActivity(intent)
            }
        })

        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (serviceAdapter.getItemViewType(position)) {
                        1 -> 1
                        2 -> 2 //number of columns of the grid
                        else -> -1
                    }
            }
        }

        rvService.layoutManager = layoutManager
        rvService.adapter = serviceAdapter

        ivTop.loadUrl("http://allwashes.com/service_me/img/2.png")
//        initSlider()
        categoryUser()
    }


//    private fun initSlider() {
//        val urlList = ArrayList<String>()
//        urlList.add("http://allwashes.com/service_me/img/1.png")
//        urlList.add("http://allwashes.com/service_me/img/2.png")
//
//
//        for (url in urlList) {
//            val textSliderView = DefaultSliderView(this)
//            textSliderView
//                    .image(url).scaleType = BaseSliderView.ScaleType.Fit
//
//            textSliderView.bundle(Bundle())
//
//            slider.addSlider(textSliderView)
//        }
//        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
//        slider.setDuration(4000)
//    }

    private fun categoryUser() {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.categoryUser(signUpRequest)
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
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (response.obj.isJsonArray) {
                            scrollView.visible()
                            val bean = Gson().fromJson<ArrayList<CategoryResult>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<CategoryResult>>() {}.type)
                            serviceAdapter.clear()
                            serviceAdapter.addList(bean)
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
        toast(R.string.message_error_connection)
    }
}
