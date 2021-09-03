package com.example.kotlinlaravelapirestful.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.databinding.FragmentUserDashboardBinding


class UserDashboardFragment : Fragment(R.layout.fragment_user_dashboard) {
    private lateinit var binding : FragmentUserDashboardBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDashboardBinding.bind(view)
    }
}