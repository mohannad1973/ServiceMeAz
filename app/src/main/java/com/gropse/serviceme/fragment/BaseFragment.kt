package com.gropse.serviceme.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import com.gropse.serviceme.R
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {


    var mProgressDialog: ProgressDialog? = null
    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mProgressDialog = ProgressDialog(activity)
        mProgressDialog?.setMessage(getString(R.string.message_loading))
        mProgressDialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mProgressDialog != null) {
            mProgressDialog?.dismiss()
        }
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable?.dispose()
            compositeDisposable = null
        }
    }

    /**
     * Load Screen Data
     */
    fun loadScreenData() {}

    //    @Override
    //    public void startActivity(Intent intent) {
    //        super.startActivity(intent);
    //        mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    //    }
}
