package com.example.kotlinlaravelapirestful.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.databinding.FragmentUserLoginBinding


class UserLoginFragment : Fragment(R.layout.fragment_user_login) {
    private lateinit var binding : FragmentUserLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserLoginBinding.bind(view)


        binding.btnSingUp.setOnClickListener{
            findNavController().navigate(R.id.action_userLoginFragment_to_userRegisterFragment)
        }

    }




}