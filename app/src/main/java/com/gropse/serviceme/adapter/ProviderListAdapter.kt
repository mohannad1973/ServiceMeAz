package com.gropse.serviceme.adapter;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.Providers
import com.gropse.serviceme.utils.loadUrl
import com.gropse.serviceme.utils.roundDecimal
import kotlinx.android.synthetic.main.list_item_service_provider_list.view.*
import java.util.*

class ProviderListAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<ProviderListAdapter.CategoryViewHolder>() {
    private val list = ArrayList<Providers>()

    companion object {
        val NONE = 0
        val ACCEPT = 1
        val REJECT = 2
        val FEEDBACK = 3
    }

    interface OnItemClick {
        fun onClick(bean: Providers, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_service_provider_list, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(newList: ArrayList<Providers>) {
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
        }

        fun onBind(position: Int) {
            itemView.tag = position
            val bean = list[position]

            itemView.ivImage.loadUrl(bean.image)
            itemView.tvWorkerName.text = bean.name
            itemView.tvDistance.roundDecimal(bean.distance)
            itemView.btnPrice.text = "SAR 100"
        }

        override fun onClick(v: View) {
            if (listener != null) listener?.onClick(list[v.tag as Int], v.tag as Int)
        }
    }
}
