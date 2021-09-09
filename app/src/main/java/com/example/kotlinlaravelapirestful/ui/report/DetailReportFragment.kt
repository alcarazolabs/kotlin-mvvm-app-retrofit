package com.example.kotlinlaravelapirestful.ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.databinding.FragmentDetailReportBinding
import com.example.primerappmvvmretrofitkotlin.application.AppConstants


class DetailReportFragment : Fragment(R.layout.fragment_detail_report) {
    private val args by navArgs<DetailReportFragmentArgs>()
    private lateinit var binding : FragmentDetailReportBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailReportBinding.bind(view)
        //Establecer deta a la vista
        binding.txtDetailReport.text = args.description
        Glide.with(requireContext()).load(AppConstants.BASE_URL_IMG+args.photo).centerCrop().into(binding.imgDetailReport)
        binding.txtDate.text = args.createdAt
    }
}