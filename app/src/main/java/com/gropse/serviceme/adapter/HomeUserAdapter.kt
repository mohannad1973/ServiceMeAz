package com.gropse.serviceme.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gropse.serviceme.R
import com.gropse.serviceme.pojo.CategoryResult
import com.gropse.serviceme.pojo.Providers
import com.gropse.serviceme.utils.circularDrawable
import com.gropse.serviceme.utils.loadUrl
import kotlinx.android.synthetic.main.list_item_home.view.*
import java.util.ArrayList

class HomeUserAdapter(private var listener: OnItemClick?) : RecyclerView.Adapter<HomeUserAdapter.CategoryViewHolder>() {
    private val list = ArrayList<CategoryResult>()

    interface OnItemClick {
        fun onClick(bean: CategoryResult, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_home, parent, false))
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
            itemView.tvName.text = bean.name
            itemView.tvName.circularDrawable()
//            itemView.btnPrice.text = bean
        }

        override fun onClick(v: View) {
            if (listener != null) listener?.onClick(list[v.tag as Int], v.tag as Int)
        }
    }
}