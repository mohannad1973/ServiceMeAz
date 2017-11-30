package com.gropse.serviceme.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.content.res.AppCompatResources
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gropse.serviceme.BuildConfig
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.OptionActivity
import com.gropse.serviceme.pojo.BaseResponse
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.drawable(start: Int? = null, top: Int? = null, end: Int? = null, bottom: Int? = null) {
    post {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (start != null) AppCompatResources.getDrawable(context, start) else null,
                if (top != null) AppCompatResources.getDrawable(context, top) else null,
                if (end != null) AppCompatResources.getDrawable(context, end) else null,
                if (bottom != null) AppCompatResources.getDrawable(context, bottom) else null)
    }
}

fun ImageView.loadUrl(url: String?, resId: Int = R.drawable.ic_user_profile, isCircular: Boolean = false) {
    if (!url.isNullOrBlank()) {
        Glide.with(context).load(url).asBitmap().placeholder(resId).into(object : ImageViewTarget<Bitmap>(this) {
            override fun setResource(resource: Bitmap) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context!!.resources, resource)
                circularBitmapDrawable.isCircular = isCircular
                setImageDrawable(circularBitmapDrawable)
            }
        })
    }
}

fun ImageView.drawable(resId: Int) {
    setImageDrawable(AppCompatResources.getDrawable(context, resId))
}

fun Context.printHashKey() {
    try {
        val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        info.signatures.forEach {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(it.toByteArray())
            log("hashkey", Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT))
        }
    } catch (e: NameNotFoundException) {
        e.printStackTrace()
    }
}


fun log(tag: String?, message: String?) {
    try {
        if (BuildConfig.LOG_ENABLE && !message.isNullOrBlank() && !tag.isNullOrBlank()) {
            Log.e(tag, message)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.toast(message: Any?) {
    if (message is Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    } else if (message is String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.isNetworkAvailable(isToNotifyUser: Boolean = true): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo
    val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

    if (isToNotifyUser && !isConnected)
        toast(R.string.message_string_no_network_message)

    return isConnected
}

fun TextView.spannableText(fulltext: String, subtext: String) {
    setText(fulltext, TextView.BufferType.SPANNABLE)
    val str = text as Spannable
    val i = fulltext.toLowerCase().indexOf(subtext.toLowerCase())
    str.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)), i, i + subtext.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun TextView.spannableBoldText(fulltext: String, boldText: String) {
    setText(fulltext, TextView.BufferType.SPANNABLE)
    val str = text as Spannable
    val i = fulltext.toLowerCase().indexOf(boldText.toLowerCase())

    val bss = StyleSpan(Typeface.BOLD) // Span to make text bold
    str.setSpan(bss, i, i + boldText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE) // make first 4 characters Bold

    //        final StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC); //Span to make text italic
    //        sb.setSpan(iss, 4, 6, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make last 2 characters Italic
    //        str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.color1)), i, i + boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
}

fun TextView.string(): String {
    return text.toString().trim { it <= ' ' }
}

fun TextView.textColor(colorId: Int = R.color.colorGrey) {
    setTextColor(ContextCompat.getColor(context, colorId))
}

fun View.backgroundColor(colorId: Int = R.color.colorPrimary) {
    setBackgroundColor(ContextCompat.getColor(context, colorId))
}

//fun TextView.price(price: String?) {
//    text = context.getString(R.string.gbp, price)
//}

fun TextView.formatStringDateTime(time: String?, fmt: String = "dd/MMM/yyyy") {
    var date = Date()
    val from = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val to = SimpleDateFormat(fmt, Locale.US)
    try {
        date = from.parse(time)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    text = to.format(date)
}

fun TextView.formatMillisDateTime(millis: Long, fmt: String = "dd/MMM/yyyy") {
    text = SimpleDateFormat(fmt, Locale.US).format(Date(millis*1000))
}

fun formatDateTimeToMillis(date: String, fmt: String = "yyyy/MM/dd HH:mm:ss"):Long {
    return SimpleDateFormat(fmt, Locale.US).parse(date).time
}

fun TextView.leftTime(millis: Long) {
    var milliSeconds = millis
    val days = TimeUnit.MILLISECONDS.toDays(milliSeconds)
    milliSeconds -= TimeUnit.DAYS.toMillis(days)
    val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
    milliSeconds -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
    milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
    text = String.format(Locale.US, "%02d:%02d", minutes, seconds)
//    return when {
//        days != 0L -> String.format(Locale.US, "%02dd %02dh %02dm %02ds", days, hours, minutes, seconds)
//        hours != 0L -> String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds)
//        minutes != 0L -> String.format(Locale.US, "%02dm %02ds", minutes, seconds)
//        else -> String.format(Locale.US, "%02ds", seconds)
//    }
}

fun Double.roundToDecimalPlaces() =
        BigDecimal(this).setScale(0, BigDecimal.ROUND_HALF_UP).toDouble()

fun TextView.roundDecimal(double: Double) {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.CEILING
    text = String.format("%s %s", df.format(double), context.getString(R.string.km))
}

fun Activity.hideKeyboard() {
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Activity.isAppInstalled(intent: Intent, openPlayStore: Boolean): Boolean {
    val packageManager = packageManager
    val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    val isIntentSafe = activities.size > 0
    if (isIntentSafe)
        return true
    if (openPlayStore) {
        val goToMarket = Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + "com.google.android.apps.maps"))
        startActivity(goToMarket)
    }
    return false
}

//fun String.isNullOrEmpty(stringToCheck: String?): Boolean {
//    return stringToCheck == null || TextUtils.isEmpty(stringToCheck)
//}

fun isLollipopOrAbove(func: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        func()
    }
}
//
//val Activity.customApplication: CustomApplication
//    get() = application as CustomApplication
//
//val Fragment.customApplication: CustomApplication
//    get() = activity.application as CustomApplication

fun View.noShapeGradient() {
    post({
        val startColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val endColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(startColor, endColor))
        background = bg
    })
}

fun View.circularGradient() {
    post({
        val startColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val endColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(startColor, endColor))
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = (height / 2).toFloat()
            background = bg
        }
    })
}

fun View.circularLeftSideGradient() {
    post({
        val startColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val endColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(startColor, endColor))
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    val f = floatArrayOf((height / 2).toFloat(), (height / 2).toFloat(), 0f, 0f, 0f, 0f, height.toFloat() / 2, height.toFloat() / 2)
                    bg.cornerRadii = f
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            val f = floatArrayOf((height / 2).toFloat(), (height / 2).toFloat(), 0f, 0f, 0f, 0f, height.toFloat() / 2, height.toFloat() / 2)
            bg.cornerRadii = f
            background = bg
        }
    })
}

fun View.circularRightSideGradient() {
    post({
        val startColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val endColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(startColor, endColor))
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    val f = floatArrayOf(0f, 0f, (height / 2).toFloat(), (height / 2).toFloat(), height.toFloat() / 2, height.toFloat() / 2, 0f, 0f)
                    bg.cornerRadii = f
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            val f = floatArrayOf(0f, 0f, (height / 2).toFloat(), (height / 2).toFloat(), height.toFloat() / 2, height.toFloat() / 2, 0f, 0f)
            bg.cornerRadii = f
            background = bg
        }
    })
}

fun View.circularBorderGradient() {
    post({
        val color = ContextCompat.getColor(context, R.color.colorWhite)
        val stroke = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.setStroke(3, stroke)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = (height / 2).toFloat()
            background = bg
        }
    })
}

fun View.circularLeftSideBorderGradient() {
    post({
        val color = ContextCompat.getColor(context, R.color.colorWhite)
        val stroke = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.setStroke(3, stroke)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    val f = floatArrayOf((height / 2).toFloat(), (height / 2).toFloat(), 0f, 0f, 0f, 0f, height.toFloat() / 2, height.toFloat() / 2)
                    bg.cornerRadii = f
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            val f = floatArrayOf((height / 2).toFloat(), (height / 2).toFloat(), 0f, 0f, 0f, 0f, height.toFloat() / 2, height.toFloat() / 2)
            bg.cornerRadii = f
            background = bg
        }
    })
}

fun View.circularRightSideBorderGradient() {
    post({
        val color = ContextCompat.getColor(context, R.color.colorWhite)
        val stroke = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.setStroke(3, stroke)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    val f = floatArrayOf(0f, 0f, (height / 2).toFloat(), (height / 2).toFloat(), height.toFloat() / 2, height.toFloat() / 2, 0f, 0f)
                    bg.cornerRadii = f
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            val f = floatArrayOf(0f, 0f, (height / 2).toFloat(), (height / 2).toFloat(), height.toFloat() / 2, height.toFloat() / 2, 0f, 0f)
            bg.cornerRadii = f
            background = bg
        }
    })
}

fun View.circularFacebookGradient() {
    post({
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = (height / 2).toFloat()
            background = bg
        }
    })
}

fun View.circularGoogleGradient() {
    post({
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = (height / 2).toFloat()
            background = bg
        }
    })
}

fun View.squareBorderGradient(strokeWidth: Int = 2, colorId: Int = R.color.colorGrey) {
    post({
        val color = ContextCompat.getColor(context, R.color.colorWhite)
        val stroke = ContextCompat.getColor(context, colorId)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.setStroke(strokeWidth, stroke)
        background = bg
    })
}

//fun View.dottedLineDrawable(shape: Int, colorId: Int) {
//    post({
//        val color = ContextCompat.getColor(context, colorId)
//        val res = context.resources
//        val bg = GradientDrawable()
//        bg.shape = shape
//        bg.setStroke(res.getDimensionPixelSize(R.dimen.dph2), color, res.getDimensionPixelSize(R.dimen.dpw6).toFloat(), res.getDimensionPixelSize(R.dimen.dpw3).toFloat())
//        background = bg
//    })
//}

fun View.circularDrawable(color: Int = R.color.colorPrimary) {
    post({
        val bg = GradientDrawable()
        bg.setColor(ContextCompat.getColor(context, color))
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = (height / 2).toFloat()
            background = bg
        }
    })
}

fun View.circularBorderDrawable(strokeWidth: Int = 3, dimenId: Int = 0) {
    post({
        val color = ContextCompat.getColor(context, R.color.colorWhite)
        val stroke = ContextCompat.getColor(context, R.color.colorPrimary)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.setStroke(strokeWidth, stroke)
        val radius = if (dimenId == 0) 0F else resources.getDimension(dimenId)
        if (height == 0) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    bg.cornerRadius = (height / 2).toFloat()
                    background = bg
                    removeOnLayoutChangeListener(this)
                }
            })
        } else {
            bg.cornerRadius = if (radius == 0F) (height / 2).toFloat() else (radius / 2)
            background = bg
        }
    })
}

fun <T> getResult(response: BaseResponse): ArrayList<T> {
    return Gson().fromJson<ArrayList<T>>(response.obj.asJsonArray.toString(), object : TypeToken<ArrayList<T>>() {}.type)
}

fun Activity.logout(){
    Prefs(this).clear()
    val intent = Intent(this, OptionActivity::class.java)
    startActivity(intent)
    this.finishAffinity()
}