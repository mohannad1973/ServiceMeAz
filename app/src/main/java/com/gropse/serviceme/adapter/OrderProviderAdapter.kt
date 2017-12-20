package com.gropse.serviceme.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.CountDownTimer
import android.support.annotation.UiThread
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.gropse.serviceme.MyApplication
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.AppConstants
import com.gropse.serviceme.utils.CustomGeocoder
import com.gropse.serviceme.utils.Prefs
import kotlinx.android.synthetic.main.item_orders.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderProviderAdapter(private var type: String?, private var context: Context, private var listener: OnItemClick?) : RecyclerView.Adapter<OrderProviderAdapter.CategoryViewHolder>() {
    private val list = ArrayList<OrderResult>()

    private val geo = CustomGeocoder()

    var currentLat = 0.0f
    var currentLon = 0.0f

    fun getMyList(): ArrayList<OrderResult> {
        return list
    }

    companion object {
        val NONE: Int = 0
        val ACCEPT = 1
        val REJECT = 2
        val FEEDBACK = 3
    }

    interface OnItemClick {
        fun onClick(bean: OrderResult, action: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_orders, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        onBind(position, holder)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(newList: ArrayList<OrderResult>) {
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun onBind(position: Int, holder: CategoryViewHolder) {
/*

        holder!!.itemView!!.setOnClickListener(this)
        holder!!.itemView!!.btnAccept.setOnClickListener(this)
        holder!!.itemView!!.btnReject.setOnClickListener(this)
        holder!!.itemView!!.btnFeedback.setOnClickListener(this)
        holder!!.itemView!!.serviceContainer.setOnClickListener(this)

*/

        //holder!!.itemView!!.tag = position
        //holder!!.itemView!!.btnAccept.tag = position
        //holder!!.itemView!!.btnReject.tag = position
        //holder!!.itemView!!.btnFeedback.tag = position

        val bean = list[position]
        holder!!.itemView!!.serviceContainer.setOnClickListener{
            listener?.onClick(list[position], when (holder!!.itemView!!.serviceContainer.id) {
                R.id.btnAccept -> ACCEPT
                R.id.btnReject -> REJECT
                R.id.btnFeedback -> FEEDBACK
                R.id.serviceContainer -> NONE
                else -> NONE
            })
        }

        val distanceFl = geo.kmDistanceBetweenPoints(currentLat, currentLon, bean.latitude.toFloat(), bean.longitude.toFloat())
        //val distanceStr = "$distanceFl"

        holder!!.itemView!!.ivProvider.loadUrl(bean.image)
        holder!!.itemView!!.tvName.text = bean.name
        holder!!.itemView!!.tvLocation.text = bean.location
        holder!!.itemView!!.tvDistance.roundDecimal(distanceFl)
//        holder!!.itemView!!.tvDistance.text = ""++" KM"
        /*holder!!.itemView!!.tvTime.leftTime(if (bean.createdDate == -1L) 0 else bean.createdDate)*/

        holder!!.itemView!!.tvTime.text = timeLeft(bean.createdDate)
        holder!!.itemView!!.tvType.text = bean.serType
        holder!!.itemView!!.tvDate.text = formatMillisDateTime(bean.serTime, "dd/MM/yyyy  HH:mm")
        holder!!.itemView!!.tvReason.text = bean.reason
        holder!!.itemView!!.squareBorderGradient(2, R.color.colorGrey)

        when (type) {
            AppConstants.SERVICE_REQUEST -> {
                holder!!.itemView!!.llServiceType.visibility = View.GONE
                holder!!.itemView!!.llDateTime.visibility = View.GONE
                holder!!.itemView!!.llReason.visibility = View.GONE
                holder!!.itemView!!.btnFeedback.visibility = View.GONE
                if (bean.timeLeft == -1L) {
                    holder!!.itemView!!.btnAccept.backgroundColor(R.color.colorGrey)
                    holder!!.itemView!!.btnReject.backgroundColor(R.color.colorGrey)
                    holder!!.itemView!!.tvTime.backgroundColor(R.color.colorGrey)
                } else {
                    holder!!.itemView!!.btnAccept.backgroundColor()
                    holder!!.itemView!!.btnReject.backgroundColor()
                    holder!!.itemView!!.tvTime.backgroundColor()
                }
            }
            AppConstants.SCHEDULED_ORDERS -> {
                holder!!.itemView!!.tvTime.visibility = View.GONE
                holder!!.itemView!!.llReason.visibility = View.GONE
                holder!!.itemView!!.btnFeedback.visibility = View.GONE
                holder!!.itemView!!.llAcceptReject.visibility = View.GONE
            }
            AppConstants.COMPLETED_ORDERS -> {
                holder!!.itemView!!.tvTime.visibility = View.GONE
                holder!!.itemView!!.llDateTime.visibility = View.GONE
                holder!!.itemView!!.llReason.visibility = View.GONE
                holder!!.itemView!!.llAcceptReject.visibility = View.GONE
                // holder!!.itemView!!.btnFeedback.circularDrawable()
            }
            AppConstants.ONGOING_ORDERS -> {
                holder!!.itemView!!.tvTime.visibility = View.GONE
                holder!!.itemView!!.llDateTime.visibility = View.GONE
                holder!!.itemView!!.llReason.visibility = View.GONE
                holder!!.itemView!!.btnFeedback.visibility = View.GONE
                holder!!.itemView!!.llAcceptReject.visibility = View.GONE
            }
            AppConstants.MISSING_ORDERS -> {
                holder!!.itemView!!.tvTime.visibility = View.GONE
                holder!!.itemView!!.llDateTime.visibility = View.GONE
                holder!!.itemView!!.btnFeedback.visibility = View.GONE
                holder!!.itemView!!.llAcceptReject.visibility = View.GONE
                holder!!.itemView!!.llServiceType.visibility = View.GONE
                holder!!.itemView!!.llReason.visibility = View.GONE
            }
        }
    }

/*    override fun onClick(v: View) {
        listener?.onClick(list[v.tag as Int], when (v.id) {
            R.id.btnAccept -> ACCEPT
            R.id.btnReject -> REJECT
            R.id.btnFeedback -> FEEDBACK
            R.id.serviceContainer -> NONE
            else -> NONE
        })
    }*/


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.btnAccept.setOnClickListener(this)
            itemView.btnReject.setOnClickListener(this)
            itemView.btnFeedback.setOnClickListener(this)
            itemView.serviceContainer.setOnClickListener(this)


            currentLat = MyApplication.instance.getLat()
            currentLon = MyApplication.instance.getLon()

            if (list.size > 0) {
                object : CountDownTimer((list[0].createdDate) * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val currentTime = System.currentTimeMillis()
                        for (bean in list) {
                            bean.timeLeft = (((bean.createdDate) * 1000) + (15 * 60 * 1000)) - currentTime
                            if (bean.timeLeft < 0L) {
                                bean.timeLeft = -1
                            }
                        }
                        notifyDataSetChanged()
                    }

                    override fun onFinish() {

                    }
                }.start()
            }
        }

        /*fun onBind(position: Int) {
            itemView.tag = position
            itemView.btnAccept.tag = position
            itemView.btnReject.tag = position
            itemView.btnFeedback.tag = position

            val bean = list[position]
            itemView.ivProvider.loadUrl(bean.image)
            itemView.tvName.text = bean.name
            itemView.tvLocation.text = bean.location
            itemView.tvDistance.roundDecimal(bean.distance)
            *//*itemView.tvTime.leftTime(if (bean.createdDate == -1L) 0 else bean.createdDate)*//*

            itemView.tvTime.text = timeLeft(bean.createdDate)
            itemView.tvType.text = bean.serType
            itemView.tvDate.text = formatMillisDateTime(bean.serTime, "dd/MM/yyyy  HH:mm")
            itemView.tvReason.text = bean.reason
            itemView.squareBorderGradient(2, R.color.colorGrey)

            when (type) {
                AppConstants.SERVICE_REQUEST -> {
                    itemView.llServiceType.visibility = View.GONE
                    itemView.llDateTime.visibility = View.GONE
                    itemView.llReason.visibility = View.GONE
                    itemView.btnFeedback.visibility = View.GONE
                    if (bean.timeLeft == -1L) {
                        itemView.btnAccept.backgroundColor(R.color.colorGrey)
                        itemView.btnReject.backgroundColor(R.color.colorGrey)
                        itemView.tvTime.backgroundColor(R.color.colorGrey)
                    } else {
                        itemView.btnAccept.backgroundColor()
                        itemView.btnReject.backgroundColor()
                        itemView.tvTime.backgroundColor()
                    }
                }
                AppConstants.SCHEDULED_ORDERS -> {
                    itemView.tvTime.visibility = View.GONE
                    itemView.llReason.visibility = View.GONE
                    itemView.btnFeedback.visibility = View.GONE
                    itemView.llAcceptReject.visibility = View.GONE
                }
                AppConstants.COMPLETED_ORDERS -> {
                    itemView.tvTime.visibility = View.GONE
                    itemView.llDateTime.visibility = View.GONE
                    itemView.llReason.visibility = View.GONE
                    itemView.llAcceptReject.visibility = View.GONE
                    // itemView.btnFeedback.circularDrawable()
                }
                AppConstants.ONGOING_ORDERS -> {
                    itemView.tvTime.visibility = View.GONE
                    itemView.llDateTime.visibility = View.GONE
                    itemView.llReason.visibility = View.GONE
                    itemView.btnFeedback.visibility = View.GONE
                    itemView.llAcceptReject.visibility = View.GONE
                }
                AppConstants.CANCELLED_ORDERS -> {
                    itemView.tvTime.visibility = View.GONE
                    itemView.llDateTime.visibility = View.GONE
                    itemView.btnFeedback.visibility = View.GONE
                    itemView.llAcceptReject.visibility = View.GONE
                }
            }
        }
   */
        override fun onClick(v: View) {
            listener?.onClick(list[this.adapterPosition], when (v.id) {
                R.id.btnAccept -> ACCEPT
                R.id.btnReject -> REJECT
                R.id.btnFeedback -> FEEDBACK
                R.id.serviceContainer -> NONE
                else -> NONE
            })
        }

    }

    fun timeLeft(millis: Long): String {
        var milliSeconds = millis
        val days = TimeUnit.MILLISECONDS.toDays(milliSeconds)
        milliSeconds -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun formatMillisDateTime(millis: Long, fmt: String = "dd/MMM/yyyy"): String {
        return SimpleDateFormat(fmt, Locale.US).format(Date(millis * 1000))
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

    fun TextView.roundDecimal(double: Double) {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING
        text = String.format("%s %s", df.format(double), context.getString(R.string.km))
    }

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

    fun View.backgroundColor(colorId: Int = R.color.colorPrimary) {
        setBackgroundColor(ContextCompat.getColor(context, colorId))
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
}