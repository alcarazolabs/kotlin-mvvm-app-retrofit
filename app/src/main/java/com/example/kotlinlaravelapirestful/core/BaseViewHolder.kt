package com.example.demo.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

//never used, there is not recyckerviews in this proyect by the moment
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
}