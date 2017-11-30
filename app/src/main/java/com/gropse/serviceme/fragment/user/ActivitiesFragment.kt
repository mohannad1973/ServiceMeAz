package com.gropse.serviceme.fragment.user

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.ReviewRatingActivity
import com.gropse.serviceme.activities.provider.CurrentServiceProviderActivity
import com.gropse.serviceme.activities.user.CancelActivity
import com.gropse.serviceme.activities.user.CreateRequestActivity
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.adapter.OrderUserAdapter
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.CategoryResult
import com.gropse.serviceme.pojo.CommonRequest
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_activities.*
import java.util.*

class ActivitiesFragment : BaseFragment() {

    private lateinit var orderUserAdapter: OrderUserAdapter
    private var commonRequest = CommonRequest()
    private var type: String = AppConstants.PENDING_ORDERS
    private var orderResult = OrderResult()
    private var date = ""
    private var time = ""
    private val LIKE = "like"
    private val BOOK_AGAIN = "book_again"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_activities, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeUserActivity).setUpToolbar(R.string.activity, false)

        commonRequest.userId = Prefs(activity).userId


        tvPending.circularDrawable()
        tvOngoing.circularBorderDrawable()
        tvCancelled.circularBorderDrawable()
        tvCompleted.circularBorderDrawable()
        tvFavourite.circularBorderDrawable()

        tvPending.textColor(R.color.colorWhite)
        tvOngoing.textColor(R.color.colorAccent)
        tvCompleted.textColor(R.color.colorAccent)
        tvCancelled.textColor(R.color.colorAccent)
        tvFavourite.textColor(R.color.colorAccent)

        tvPending.setOnClickListener {
            resetViews()
            type = AppConstants.PENDING_ORDERS
            tvPending.circularDrawable()
            tvPending.textColor(R.color.colorWhite)
            pendingUser()
        }

        tvOngoing.setOnClickListener {
            resetViews()
            type = AppConstants.ONGOING_ORDERS
            tvOngoing.circularDrawable()
            tvOngoing.textColor(R.color.colorWhite)
            ongoingUser()
        }

        tvCancelled.setOnClickListener {
            resetViews()
            type = AppConstants.CANCELLED_ORDERS
            tvCancelled.circularDrawable()
            tvCancelled.textColor(R.color.colorWhite)
            cancelledUser()
        }

        tvFavourite.setOnClickListener {
            resetViews()
            type = AppConstants.FAVOURITE_ORDERS
            tvFavourite.circularDrawable()
            tvFavourite.textColor(R.color.colorWhite)
            favouriteUser()
        }

        tvCompleted.setOnClickListener {
            resetViews()
            type = AppConstants.COMPLETED_ORDERS
            tvCompleted.circularDrawable()
            tvCompleted.textColor(R.color.colorWhite)
            completeUser()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        orderUserAdapter = OrderUserAdapter(object : OrderUserAdapter.OnItemClick {
            override fun onClick(bean: OrderResult, action: Int) {
                orderResult = bean
                when (action) {
                    OrderUserAdapter.LIKE -> {
                        commonRequest.serId = bean.serId
                        commonRequest.value = if (bean.isFavourite == 1) 0 else 1
                        likeUser()
                    }
                    OrderUserAdapter.STATUS -> {
                        val intent = Intent(activity, CurrentServiceProviderActivity::class.java)
                        intent.putExtra(OrderResult::class.java.name, bean)
                        startActivity(intent)
                    }
                    OrderUserAdapter.CANCEL -> {
                        val intent = Intent(activity, CancelActivity::class.java)
                        intent.putExtra(OrderResult::class.java.name, bean)
                        startActivity(intent)
                    }
                    OrderUserAdapter.RESCHEDULE -> {
                        commonRequest.serId = bean.serId
                        openDatePicker()
                    }
                    OrderUserAdapter.FEEDBACK -> {
                        val intent = Intent(activity, ReviewRatingActivity::class.java)
                        intent.putExtra(OrderResult::class.java.name, bean)
                        intent.putExtra(AppConstants.SCREEN, AppConstants.PROVIDER)
                        startActivity(intent)
                    }
                    OrderUserAdapter.BOOK_AGAIN -> {

                    }
                }
            }
        })
        recyclerView.adapter = orderUserAdapter

        pendingUser()
    }

    private fun resetViews() {
        tvPending.circularBorderDrawable()
        tvOngoing.circularBorderDrawable()
        tvCancelled.circularBorderDrawable()
        tvCompleted.circularBorderDrawable()
        tvFavourite.circularBorderDrawable()

        tvPending.textColor(R.color.colorAccent)
        tvOngoing.textColor(R.color.colorAccent)
        tvCompleted.textColor(R.color.colorAccent)
        tvCancelled.textColor(R.color.colorAccent)
        tvFavourite.textColor(R.color.colorAccent)
    }

    private fun pendingUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.pendingUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }

    }

    private fun ongoingUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.ongoingUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }

    }

    private fun completeUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.completedUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }

    }

    private fun cancelledUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.cancelledUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }

    }

    private fun favouriteUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.favouritesUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, OrderResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }

    }

    private fun likeUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.likeUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, LIKE)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun rescheduleUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.rescheduleUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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

    private fun serviceDetailsUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.serviceDetailsUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, BOOK_AGAIN)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun onResponse(response: Any, resType: String = "") {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> activity.logout()
                    3 -> {
                        if (resType == OrderResult::class.java.name) {
                            orderUserAdapter.clear()
                            tvNoService.visible()
                        } else {
                            activity.toast(response.message)
                        }
                    }
                    200 -> {
                        if (response.obj.isJsonArray && resType == OrderResult::class.java.name) {
                            recyclerView.visible()
                            tvNoService.gone()
                            val bean = Gson().fromJson<ArrayList<OrderResult>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<OrderResult>>() {}.type)
                            orderUserAdapter.clear()
                            orderUserAdapter.addList(type, bean)
                        }
                        else if (response.obj.isJsonObject && resType == LIKE) {
                            orderResult.isFavourite = commonRequest.value
                            orderUserAdapter.notifyDataSetChanged()
                        }
                        else if (response.obj.isJsonObject && resType == BOOK_AGAIN) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), CategoryResult::class.java)
                            val intent = Intent(activity, CreateRequestActivity::class.java)
                            intent.putExtra(CategoryResult::class.java.name, bean)
                            startActivity(intent)
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

    companion object {

        fun newInstance(): ActivitiesFragment {
            return ActivitiesFragment()
        }
    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(activity, { _, y, m, d ->
            date = StringBuilder().append(y).append("/").append(String.format(Locale.US, "%02d", m + 1)).append("/").append(String.format(Locale.US, "%02d", d)).toString()
            openTimePicker()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        val cal = Calendar.getInstance()
        cal.time = Date()
        datePickerDialog.datePicker.minDate = cal.timeInMillis
        log("date", cal.time.toString() + "")
        datePickerDialog.show()
    }

    private fun openTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            run {
                time = selectedHour.toString() + ":" + selectedMinute + ":00"
                commonRequest.time = if (date.isNotBlank() && time.isNotBlank()) formatDateTimeToMillis(date + " " + time) else 0L
                dialogReschedule()
            }
        }, hour, minute, true)//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private fun dialogReschedule() {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle(R.string.reschedule)
        dialogBuilder.setMessage(getString(R.string.message_schedule, date, time))
        dialogBuilder.setPositiveButton("Confirm", { dialogInterface, _ ->
            run {
                rescheduleUser()
                dialogInterface.dismiss()
            }
        })
        dialogBuilder.setNegativeButton(R.string.cancel, { dialogInterface, _ ->
            run {
                date = ""
                time = ""
                dialogInterface.dismiss()
            }
        }).create().show()
    }

}