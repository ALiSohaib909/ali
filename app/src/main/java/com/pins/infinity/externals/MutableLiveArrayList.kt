package com.pins.infinity.externals

import androidx.lifecycle.MutableLiveData

/**
 * Created by Pavlo Melnyk on 28.11.2018.
 */
class MutableLiveArrayList<T> : MutableLiveData<ArrayList<T>>() {

    private val items = ArrayList<T>()

    fun getSize(): Int = items.size

    fun getItem(position: Int): T = items[position]

    fun add(element: T) {
        items.add(element)
        update()
    }

    fun remove(element: T){
        items.remove(element)
        update()
    }

    fun addAll(list: List<T>) {
        items.addAll(list)
        update()
    }

    fun clear() {
        items.clear()
        update()
    }

    fun isEmpty() = items.isEmpty()

    fun indexOf(element: T) : Int = items.indexOf(element)

    private fun update() {
        value = items
    }
}