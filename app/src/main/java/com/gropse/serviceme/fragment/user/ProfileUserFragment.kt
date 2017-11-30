package com.gropse.serviceme.fragment.user

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.ChangePasswordActivity
import com.gropse.serviceme.activities.both.OptionActivity
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.*
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_user.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileUserFragment : BaseFragment() {

    private val editProfileRequest = EditProfileUserRequest()
    private var commonRequest = CommonRequest()
    private val PLACE_PICKER_REQUEST = 1
    private var isEditable = false

    companion object {
        fun newInstance(): ProfileUserFragment {
            return ProfileUserFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_profile_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeUserActivity).setUpToolbar(R.string.profile, false)

        etName.circularBorderDrawable(5)
        etMobile.circularBorderDrawable(5)
        tvEmail.circularBorderDrawable(5)
        tvLocation.circularBorderDrawable(5)
        tvCity.circularBorderDrawable(5)

        tvChange.circularDrawable()
        tvLogout.circularDrawable()

        etName.drawable(R.drawable.ic_user)
        etMobile.drawable(R.drawable.ic_mobile)
        tvEmail.drawable(R.drawable.ic_email)
        tvLocation.drawable(R.drawable.ic_pin)
        tvCity.drawable(R.drawable.ic_pin)

        ivProfile.setOnClickListener { showImageChooser() }

        tvLocation.setOnClickListener {
            if (isEditable) {
                try {
                    val builder = PlacePicker.IntentBuilder()
                    startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        tvChange.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            intent.putExtra(AppConstants.SCREEN, AppConstants.CHANGE_PASSWORD)
            startActivity(intent)
        }

        tvLogout.setOnClickListener {
           logout()
        }

        commonRequest.userId = Prefs(activity).userId
        commonRequest.providerType = Prefs(activity).userType
        commonRequest.deviceId = Prefs(activity).deviceId

        profileUser()

    }

    private fun showImageChooser() {
        val items = arrayOf("Camera", "Gallery")
        val adapter = ArrayAdapter(activity, android.R.layout.select_dialog_item, items)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Image")
        builder.setAdapter(adapter) { dialog, item ->
            if (item == 0) {
                ImageUtility.getImageBean(activity, ImageUtility.pickSingle(activity, ImageUtility.CAMERA))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            } else {
                ImageUtility.getImageBean(activity, ImageUtility.pickSingle(activity, ImageUtility.GALLERY))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setImage(bean: ImageUtility.ImageBean) {
        ivProfile.setImageBitmap(bean.bitmap)

        val type = RequestBody.create(MediaType.parse("text/plain"), "0")

        val file = File(bean.imagePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imageFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        addFile(type, imageFile)
    }

    private fun addFile(type: RequestBody, file: MultipartBody.Part) {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.addFile(type, file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response)
                    }, { throwable ->
                        throwable.printStackTrace()
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            val place = PlacePicker.getPlace(activity, data)
            commonRequest.latitude = place.latLng.latitude.toString()
            commonRequest.longitude = place.latLng.longitude.toString()
            tvLocation.text = place.name
        }
    }

    private fun updateUI(bean: ProfileResult) {
        ivProfile.loadUrl(bean.image)
        etName.setText(bean.name)
        etMobile.setText(bean.mobile)
        tvEmail.text = bean.email
        tvLocation.text = bean.location
        tvCity.text = bean.city
        enableEdit(false)
        (activity as HomeUserActivity).editEnable(false)
    }

    fun enableEdit(isEnable: Boolean) {
        isEditable = isEnable
        etName.isEnabled = isEnable
        etMobile.isEnabled = isEnable
    }

    private fun isValidData(): Boolean {
        editProfileRequest.name = etName.string()
        editProfileRequest.mobile = etMobile.string()
        editProfileRequest.location = tvLocation.string()

        if (editProfileRequest.name.isBlank()) {
            activity.toast(R.string.message_enter_name)
            return false
        } else if (editProfileRequest.mobile.isBlank()/* || signUpRequest.mobile.length < resources.getInteger(R.integer.mobile_length)*/) {
            activity.toast(R.string.message_enter_valid_mobile)
            return false
        }
        editProfileRequest.userId = Prefs(activity).userId
        return true
    }

    fun editProfileUser() {
        if (isValidData()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.editProfileUser(Prefs(activity).deviceId, Prefs(activity).securityToken, editProfileRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, ProfileResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun profileUser() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.getProfileUser(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response, ProfileResult::class.java.name)
                    }, { throwable ->
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun logout() {
        if (activity.isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.logout(Prefs(activity).deviceId, Prefs(activity).securityToken, commonRequest)
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

    private fun onResponse(response: Any, resType: String = "") {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> activity.toast(response.message)
                    200 -> {
                        if (response.obj.isJsonObject && resType == ProfileResult::class.java.name) {
                            if (isEditable){
                                (activity as HomeUserActivity).editEnable(false)
                            } else{
                                scrollView.visible()
                                val bean = Gson().fromJson(response.obj.asJsonObject.toString(), ProfileResult::class.java)
                                updateUI(bean)
                            }
                        } else if (response.obj.isJsonObject && resType == FileResult::class.java.name) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), FileResult::class.java)
                            editProfileRequest.image = bean?.id ?: ""
                        } else if (resType.isBlank()) {
                            activity.logout()
                        }
                    }
                }
            }
            progressBar.gone()
//            frameLayout.gone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        progressBar.gone()
//        frameLayout.gone()
        activity.toast(R.string.message_error_connection)
    }
}