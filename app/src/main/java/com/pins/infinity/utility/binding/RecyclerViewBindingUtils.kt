package com.pins.infinity.utility.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pins.infinity.adapters.RecyclerAdapter

/**
 * Created by Pavlo Melnyk on 28.11.2018.
 */
@BindingAdapter("app:adapter")
fun setAdapter(recyclerView: RecyclerView, adapter: RecyclerAdapter<*>) {
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
}