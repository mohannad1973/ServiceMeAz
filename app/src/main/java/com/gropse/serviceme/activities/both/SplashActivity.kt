package com.gropse.serviceme.activities.both

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.provider.HomeProviderActivity
import com.gropse.serviceme.activities.user.HomeUserActivity
import com.gropse.serviceme.utils.AppConstants
import com.gropse.serviceme.utils.Prefs
import com.gropse.serviceme.utils.log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class SplashActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val TAG = SplashActivity::class.java.simpleName
    private lateinit var mActivity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mActivity = this
        methodRequiresTwoPermission()
//        showSplash()
//        printHashKey()

//        PermissionsManager.get()
//                .requestLocationPermission()
//                .subscribe { permissionsResult ->
//                    if (permissionsResult.isGranted) { // always true pre-M
//                        showSplash()
//                    } else {
//                        finish()
//                    }
//                    if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
//
//                    }
//
//                }
    }

    private fun showSplash() {
        launch(CommonPool) {
            val refreshedToken = FirebaseInstanceId.getInstance().token
            Prefs(mActivity).deviceToken = refreshedToken ?: ""
            log(TAG, refreshedToken)
            delay(3000)
//            startActivity(Intent(mActivity, if (Prefs(mActivity).login) (if (Prefs(mActivity).userType == AppConstants.TYPE_USER) HomeUserActivity::class.java else HomeProviderActivity::class.java) else OptionActivity::class.java))
            startActivity(Intent(mActivity, if (Prefs(mActivity).login) (if (Prefs(mActivity).userType == AppConstants.TYPE_USER) HomeUserActivity::class.java else HomeProviderActivity::class.java) else if (Prefs(mActivity).isFirstTimeLaunch) ServiceMeIntroActivity::class.java else OptionActivity::class.java))
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(AppConstants.RC_ALL_NECESSARY_PERMISSION)
    private fun methodRequiresTwoPermission() {
        val perms = AppConstants.PERMISSION_ALL_NECESSARY
        if (EasyPermissions.hasPermissions(this, *perms)) {
            showSplash()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(mActivity, getString(R.string.rationale_camera_storage_location), AppConstants.RC_ALL_NECESSARY_PERMISSION, *perms)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        showSplash()
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        finish()
    }
}
