package com.pins.infinity.adapters

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pins.infinity.BR
import com.pins.infinity.externals.MutableLiveArrayList
import com.pins.infinity.viewModels.base.BaseRowViewModel

/**
 * Created by Pavlo Melnyk on 28.11.2018.
 */
class RecyclerAdapter<T : BaseRowViewModel>(
        private val itemsList: MutableLiveArrayList<T>,
        private val parameters: () -> Map<Int, Int>)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var onRowClickListener: OnRowClickListener? = null

    init {
        itemsList.observeForever { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val layoutId = parameters.invoke()[viewType]!!
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false)
        return ViewHolder(binding, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.binding.setVariable(BR.viewModel, item)
        viewHolder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = itemsList.getSize()

    private fun getItem(position: Int): T = itemsList.getItem(position)

    class ViewHolder(
            val binding: ViewDataBinding,
            private val recyclerAdapter: RecyclerAdapter<*>)
        : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            recyclerAdapter.onRowClickListener?.onRowClick(recyclerAdapter.getItem(adapterPosition))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item.viewType
    }

    interface OnRowClickListener {
        fun onRowClick(model: Any)
    }

}