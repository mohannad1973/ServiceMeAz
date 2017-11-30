package com.gropse.serviceme.fcm

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

import com.gropse.serviceme.utils.Prefs
import com.gropse.serviceme.utils.log

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    override fun onTokenRefresh() {

        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Prefs(this).deviceToken = refreshedToken ?: ""
        log(TAG, refreshedToken)
        if (Prefs(this).login) {
//            ApiHelper.sendRegistrationToServer()
        }
    }
}
