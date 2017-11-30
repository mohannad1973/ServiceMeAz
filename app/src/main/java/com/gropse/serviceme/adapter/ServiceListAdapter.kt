package com.gropse.serviceme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.CategoryResult
import com.gropse.serviceme.utils.drawable
import com.gropse.serviceme.utils.loadUrl
import kotlinx.android.synthetic.main.list_item_service_list.view.*
import java.util.*


class ServiceListAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<ServiceListAdapter.CategoryViewHolder>() {
    private val list = ArrayList<CategoryResult>()
    interface OnItemClick {
        fun onClick(bean: CategoryResult, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_service_list, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(newList: ArrayList<CategoryResult>) {
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
            itemView.ivItemImage.loadUrl(bean.image)
            itemView.tvItemName.text = bean.name
            itemView.tvItemName.drawable(null, null, if (bean.isChecked) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp)
        }

        override fun onClick(v: View) {
            if (listener != null) listener?.onClick(list[v.tag as Int], v.tag as Int)
        }
    }
}