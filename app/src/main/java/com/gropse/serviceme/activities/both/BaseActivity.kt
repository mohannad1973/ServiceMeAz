package com.gropse.serviceme.activities.both

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.Toolbar
import android.widget.ProgressBar
import com.gropse.serviceme.R
import com.gropse.serviceme.utils.*
import io.reactivex.disposables.CompositeDisposable
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    var mProgressDialog: ProgressDialog? = null
    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setMessage(getString(R.string.message_loading))
        mProgressDialog?.setCanceledOnTouchOutside(false)

    }

      fun removeHtmlTags(data: String): String {
        var data = data
        data = data.replace("\\<[^>]*>".toRegex(), "")
        return data
    }

    fun setUpToolbar(resId: Int, backEnable: Boolean = true, colorId: Int = R.color.colorWhite) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val tvTitle = findViewById<AppCompatTextView>(R.id.tvTitle)
        if(tvTitle!=null) {
            tvTitle.setText(resId)
            tvTitle.textColor(colorId)
        } else {
            toolbar.setTitle(resId)
            toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, colorId))
        }
        if(backEnable) {
            val drawable = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_back)
            drawable?.mutate()?.setColorFilter(ContextCompat.getColor(applicationContext, colorId), PorterDuff.Mode.SRC_IN)
            toolbar.navigationIcon = drawable
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
        try {
            if (mProgressDialog?.isShowing == true) {
                mProgressDialog?.hide()
            }
            mProgressDialog?.isShowing.let {
                mProgressDialog?.hide()
            }
            if (isFinishing) {
                mProgressDialog?.dismiss()
            }
            hideKeyboard()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    open fun onActivityDestroy() {

    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroy()
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
            compositeDisposable = null
        }
    }

    fun showProgress(mActivity: Activity?) {
        mActivity?.runOnUiThread { mProgressDialog?.show() }
    }

    fun hideProgress(mActivity: Activity?, msg: String) {
        mActivity?.runOnUiThread {
            mProgressDialog?.hide()
            if (msg.isNotBlank())
                toast(msg)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newLocale: Locale
        if (Prefs(newBase).locale.equals("")) {
            Prefs(newBase).locale = "en"
        }
        newLocale = Locale(Prefs(newBase).locale)
        val context = ContextWrapper.wrap(newBase, newLocale)
        super.attachBaseContext(context)
    }



    //    @Override
    //    public void startActivity(Intent intent) {
    //        super.startActivity(intent);
    //        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    //    }
    //
    //    @Override
    //    public void onBackPressed() {
    //        super.onBackPressed();
    //        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    //    }
}
