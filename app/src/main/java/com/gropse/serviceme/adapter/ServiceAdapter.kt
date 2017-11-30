package com.gropse.serviceme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.CategoryResult
import com.gropse.serviceme.utils.loadUrl
import kotlinx.android.synthetic.main.list_item_service.view.*
import java.util.*

class ServiceAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<ServiceAdapter.CategoryViewHolder>() {
    private val list = ArrayList<CategoryResult>()

    interface OnItemClick {
        fun onClick(bean: CategoryResult, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_service, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount % 2 == 0) 1 else if ((itemCount - 1) == position) 2 else 1 // If the item is last, `itemViewType` will be 0
//        return if ((itemCount-1)==position) 2 else 1
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
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
            itemView.ivItem.loadUrl(bean.image)
            itemView.tvServices.text = bean.name
        }

        override fun onClick(v: View) {
            if (listener != null) listener?.onClick(list[v.tag as Int], v.tag as Int)
        }
    }
}