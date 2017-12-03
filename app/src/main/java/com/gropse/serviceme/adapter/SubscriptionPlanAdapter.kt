package com.gropse.serviceme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.SubscriptionPlanResult
import com.gropse.serviceme.utils.backgroundColor
import kotlinx.android.synthetic.main.list_item_subscription_plan.view.*
import java.util.*

class SubscriptionPlanAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<SubscriptionPlanAdapter.CategoryViewHolder>() {
    private val list = ArrayList<SubscriptionPlanResult>()

    interface OnItemClick {
        fun onClick(bean: SubscriptionPlanResult, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_subscription_plan, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(newList: ArrayList<SubscriptionPlanResult>) {
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun onBind(position: Int) {
            itemView.tag = position
            val bean = list[position]
            itemView.ivPlanName.text =bean.name
            /*if (bean!!.name.equals("Subscription for 1 Months")) {
                itemView.ivPlanName.text =
            } else if (bean!!.name.equals("Subscription for 3 Months")) {
                itemView.ivPlanName.text
            } else if (bean!!.name.equals("Subscription for 6 Months")) {
                itemView.ivPlanName.text
            } else if (bean!!.name.equals("Subscription for 12 Months")) {
                itemView.ivPlanName.text
            }*/
            itemView.ivPrice.text = String.format("SAR %s",bean.price)
            itemView.backgroundColor(if (bean.isSelected) R.color.colorPrimary else R.color.colorGrey)
        }

        override fun onClick(v: View) {
            if (listener != null) listener?.onClick(list[v.tag as Int], v.tag as Int)
        }
    }
}