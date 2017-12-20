package com.gropse.serviceme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.MyApplication
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import kotlinx.android.synthetic.main.item_user_orders.view.*
import kotlinx.android.synthetic.main.list_item_service.view.*
import java.util.*

class OrderUserAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<OrderUserAdapter.CategoryViewHolder>() {
    private val list = ArrayList<OrderResult>()
    private var type: String = AppConstants.PENDING_ORDERS

    private val geo = CustomGeocoder()

    var currentLat = 0.0f
    var currentLon = 0.0f

    companion object {
        val NONE = 0
        val LIKE = 1
        val STATUS = 2
        val CANCEL = 3
        val RESCHEDULE = 4
        val FEEDBACK = 5
        val BOOK_AGAIN = 6
    }

    interface OnItemClick {
        fun onClick(bean: OrderResult, action: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_orders, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(uiType: String, newList: ArrayList<OrderResult>) {
        type = uiType
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.tvStatus.setOnClickListener(this)
            itemView.tvCancel.setOnClickListener(this)
            itemView.tvReschedule.setOnClickListener(this)
            itemView.tvFeedback.setOnClickListener(this)
            itemView.tvBookAgain.setOnClickListener(this)
            itemView.ivLike.setOnClickListener(this)

            currentLat = MyApplication.instance.getLat()
            currentLon = MyApplication.instance.getLon()
        }

        fun onBind(position: Int) {
            itemView.tag = position
            itemView.tvStatus.tag = position
            itemView.tvCancel.tag = position
            itemView.tvReschedule.tag = position
            itemView.tvFeedback.tag = position
            itemView.tvBookAgain.tag = position
            itemView.ivLike.tag = position

            val bean = list[position]

            val distanceFl = geo.kmDistanceBetweenPoints(currentLat, currentLon, bean.latitude.toFloat(), bean.longitude.toFloat())


            itemView.ivImage.loadUrl(bean.image)
            itemView.ivLike.drawable(if (bean.isFavourite == 1) R.drawable.ic_favorite_primary else R.drawable.ic_favorite_grey)
            itemView.tvNameUser.text = bean.name
            itemView.tvLocationUser.text = bean.location
            itemView.tvDistanceUser.roundDecimal(distanceFl)
            itemView.squareBorderGradient(2, R.color.colorPrimary)
            itemView.tvStatus.circularDrawable()
            itemView.tvCancel.circularDrawable()
            itemView.tvReschedule.circularDrawable()
            itemView.tvFeedback.circularDrawable()
            itemView.tvBookAgain.circularDrawable()
            when (type) {
                AppConstants.PENDING_ORDERS -> {
                    itemView.linDist.visible()
                    itemView.tvStatus.gone()
                    itemView.tvCancel.visible()
                    itemView.tvReschedule.gone()
                    itemView.tvFeedback.gone()
                    itemView.tvBookAgain.gone()
                    itemView.ivLike.gone()
                }
                AppConstants.ONGOING_ORDERS -> {
                    itemView.linDist.visible()
                    itemView.tvStatus.visible()
                    itemView.tvCancel.visible()
                    itemView.tvReschedule.visible()
                    itemView.tvFeedback.gone()
                    itemView.tvBookAgain.gone()
                    itemView.ivLike.gone()
                }
                AppConstants.COMPLETED_ORDERS -> {
                    itemView.linDist.visible()
                    itemView.tvStatus.gone()
                    itemView.tvCancel.gone()
                    itemView.tvReschedule.visible()
                    itemView.tvFeedback.visible()
                    itemView.tvBookAgain.gone()
                    itemView.ivLike.visible()
                }
                AppConstants.CANCELLED_ORDERS -> {
                    itemView.linDist.visible()
                    itemView.tvStatus.gone()
                    itemView.tvCancel.gone()
                    itemView.tvReschedule.visible()
                    itemView.tvFeedback.gone()
                    itemView.tvBookAgain.gone()
                    itemView.ivLike.gone()
                }
                AppConstants.FAVOURITE_ORDERS -> {
                    itemView.linDist.gone()
                    itemView.tvStatus.gone()
                    itemView.tvCancel.gone()
                    itemView.tvReschedule.gone()
                    itemView.tvFeedback.gone()
                    itemView.tvBookAgain.visible()
                    itemView.ivLike.gone()
                }
            }
        }

        override fun onClick(v: View) {
            listener?.onClick(list[v.tag as Int], when (v.id) {
                R.id.ivLike -> LIKE
                R.id.tvStatus -> STATUS
                R.id.tvCancel -> CANCEL
                R.id.tvReschedule -> RESCHEDULE
                R.id.tvFeedback -> FEEDBACK
                R.id.tvBookAgain -> BOOK_AGAIN
                else -> NONE
            })
        }
    }
}