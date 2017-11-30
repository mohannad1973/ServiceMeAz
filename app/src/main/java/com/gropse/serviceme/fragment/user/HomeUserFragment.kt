package com.gropse.serviceme.fragment.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.activities.user.ServiceActivity
import com.gropse.serviceme.adapter.HomeUserAdapter
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.HomeResponse
import com.gropse.serviceme.pojo.HomeResult
import com.gropse.serviceme.pojo.Providers
import com.gropse.serviceme.slider.BaseSliderView
import com.gropse.serviceme.slider.DefaultSliderView
import com.gropse.serviceme.slider.SliderLayout
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home_user.*

class HomeUserFragment : BaseFragment() {
    private var bean = HomeResult()
    private var commonRequest = CommonRequest()
    private lateinit var homeUserAdapter: HomeUserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeUserActivity).setUpToolbar(R.string.home, false)

        tvOffice.circularDrawable()
        tvHome.circularDrawable()

//
//        homeUserAdapter = HomeUserAdapter(object : HomeUserAdapter.OnItemClick{
//            override fun onClick(bean: Providers, type: Int) {
//
//            }
//        })
//        rvHome.layoutManager = LinearLayoutManager(activity)
//        rvHome.adapter = homeUserAdapter

        llOffice.setOnClickListener {
            val intent = Intent(activity, ServiceActivity::class.java)
            intent.putExtra(AppConstants.SERVICE_FOR, AppConstants.SERVICE_OFFICE)
            startActivity(intent)
        }

        llHome.setOnClickListener {
            val intent = Intent(activity, ServiceActivity::class.java)
            intent.putExtra(AppConstants.SERVICE_FOR, AppConstants.SERVICE_HOME)
            startActivity(intent)
        }

        initSlider()

        commonRequest.userId = Prefs(activity).userId
        homeUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode== Activity.RESULT_OK && data!=null){
            homeUser()
        }
    }

    private fun initSlider() {
        val urlList = ArrayList<String>()
        urlList.add("http://allwashes.com/service_me/img/1.png")
        urlList.add("http://allwashes.com/service_me/img/2.png")

        for (url in urlList) {
            val textSliderView = DefaultSliderView(activity)
            textSliderView.image(url).scaleType = BaseSliderView.ScaleType.Fit
            textSliderView.bundle(Bundle())
            slider.addSlider(textSliderView)
        }
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        slider.setDuration(4000)
    }

    private fun homeUser() {
        if (activity.isNetworkAvailable()) {
            if (activity != null) activity?.runOnUiThread { mProgressDialog?.show() }
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.homeUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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
            if (response is HomeResponse) {
                if (response.errorCode == 200) {
                    bean = response.result ?: HomeResult()
                    (activity as HomeUserActivity).setPlan(bean)
//                    homeUserAdapter.addList(bean)
                }
            }
            if (activity != null)
                activity?.runOnUiThread {
                    mProgressDialog?.hide()
                }
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        if (activity != null)
            activity?.runOnUiThread {
                mProgressDialog?.hide()
                activity.toast(R.string.message_error_connection)
            }
    }


    companion object {
        fun newInstance(): HomeUserFragment {
            return HomeUserFragment()
        }
    }
}
