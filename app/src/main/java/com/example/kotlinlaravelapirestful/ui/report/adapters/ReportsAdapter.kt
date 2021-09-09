package com.example.primerappmvvmretrofitkotlin.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demo.core.BaseViewHolder
import com.example.kotlinlaravelapirestful.data.model.ReportsResponseObj
import com.example.kotlinlaravelapirestful.databinding.ReportItemBinding
import com.example.primerappmvvmretrofitkotlin.application.AppConstants

class ReportsAdapter(
        private val reportsList: List<ReportsResponseObj>,
        private val itemClickListener: OnReportClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnReportClickListener {
        fun onReportClick(report: ReportsResponseObj)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = UsersViewHolder(itemBinding, parent.context)

        itemBinding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onReportClick(reportsList[position])
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is UsersViewHolder -> holder.bind(reportsList[position])
        }
    }

    override fun getItemCount(): Int = reportsList.size

    private inner class UsersViewHolder(val binding: ReportItemBinding, val context: Context) : BaseViewHolder<ReportsResponseObj>(binding.root) {
        override fun bind(item: ReportsResponseObj) {
            binding.txtDescription.text="${item.description}"
            binding.txtCreatedAt.text = "Fecha de Registro: ${item.created_at}"
            Glide.with(context).load(AppConstants.BASE_URL_IMG+"${item.photo}").centerCrop().into(binding.imgReport)

        }
    }
}