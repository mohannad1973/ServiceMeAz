package com.gropse.serviceme.adapter

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.OrderResult
import com.gropse.serviceme.utils.*
import kotlinx.android.synthetic.main.item_orders.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class OrderProviderAdapter(private var type: String?, private var listener: OnItemClick?) : RecyclerView.Adapter<OrderProviderAdapter.CategoryViewHolder>() {
    private val list = ArrayList<OrderResult>()

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
        holder.onBind(position)
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

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.btnAccept.setOnClickListener(this)
            itemView.btnReject.setOnClickListener(this)
            itemView.btnFeedback.setOnClickListener(this)

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

        fun onBind(position: Int) {
            itemView.tag = position
            itemView.btnAccept.tag = position
            itemView.btnReject.tag = position
            itemView.btnFeedback.tag = position

            val bean = list[position]
            itemView.ivProvider.loadUrl(bean.image)
            itemView.tvName.text = bean.name
            itemView.tvLocation.text = bean.location
            itemView.tvDistance.roundDecimal(bean.distance)
            /*itemView.tvTime.leftTime(if (bean.createdDate == -1L) 0 else bean.createdDate)*/

            itemView.tvTime.leftTime(bean.createdDate)
            itemView.tvType.text = bean.serType
            itemView.tvDate.formatMillisDateTime(bean.serTime, "dd/MM/yyyy  HH:mm")
            itemView.tvReason.text = bean.reason
            itemView.squareBorderGradient(2, R.color.colorGrey)

            when (type) {
                AppConstants.SERVICE_REQUEST -> {
                    itemView.llServiceType.gone()
                    itemView.llDateTime.gone()
                    itemView.llReason.gone()
                    itemView.btnFeedback.gone()
                    if (bean.timeLeft == -1L) {
                        itemView.btnAccept.circularDrawable(R.color.colorGrey)
                        itemView.btnReject.circularDrawable(R.color.colorGrey)
                        itemView.tvTime.backgroundColor(R.color.colorGrey)
                    } else {
                        itemView.btnAccept.circularDrawable()
                        itemView.btnReject.circularDrawable()
                        itemView.tvTime.backgroundColor()
                    }
                }
                AppConstants.SCHEDULED_ORDERS -> {
                    itemView.tvTime.gone()
                    itemView.llReason.gone()
                    itemView.btnFeedback.gone()
                    itemView.llAcceptReject.gone()
                }
                AppConstants.COMPLETED_ORDERS -> {
                    itemView.tvTime.gone()
                    itemView.llDateTime.gone()
                    itemView.llReason.gone()
                    itemView.llAcceptReject.gone()
                    itemView.btnFeedback.circularDrawable()
                }
                AppConstants.ONGOING_ORDERS -> {
                    itemView.tvTime.gone()
                    itemView.llDateTime.gone()
                    itemView.llReason.gone()
                    itemView.btnFeedback.gone()
                    itemView.llAcceptReject.gone()
                }
                AppConstants.CANCELLED_ORDERS -> {
                    itemView.tvTime.gone()
                    itemView.llDateTime.gone()
                    itemView.btnFeedback.gone()
                    itemView.llAcceptReject.gone()
                }
            }
        }

        override fun onClick(v: View) {
            listener?.onClick(list[v.tag as Int], when (v.id) {
                R.id.btnAccept -> ACCEPT
                R.id.btnReject -> REJECT
                R.id.btnFeedback -> FEEDBACK
                else -> NONE
            })
        }
    }
}