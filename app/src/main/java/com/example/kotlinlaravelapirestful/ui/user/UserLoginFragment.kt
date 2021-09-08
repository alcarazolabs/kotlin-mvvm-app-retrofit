package com.example.kotlinlaravelapirestful.ui.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.data.remote.UserDataSource
import com.example.kotlinlaravelapirestful.databinding.FragmentUserLoginBinding
import com.example.kotlinlaravelapirestful.presentation.UserViewModel
import com.example.kotlinlaravelapirestful.presentation.UserViewModelFactory
import com.example.kotlinlaravelapirestful.repository.RetrofitClient
import com.example.kotlinlaravelapirestful.repository.UserRepositoryImpl


class UserLoginFragment : Fragment(R.layout.fragment_user_login) {
    private lateinit var binding : FragmentUserLoginBinding
    //Instanciar viewModel
    private val viewModel by viewModels<UserViewModel> { UserViewModelFactory(UserRepositoryImpl(
        UserDataSource(RetrofitClient.webservice)
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserLoginBinding.bind(view)


        binding.btnSingUp.setOnClickListener{
            findNavController().navigate(R.id.action_userLoginFragment_to_userRegisterFragment)
        }

        binding.btnLogin.setOnClickListener{
            login()
        }
        setupObservers()
    }

    fun setupObservers(){
        viewModel.apiError.observe(viewLifecycleOwner, {
            when(it){
                is Resource.apiError->{
                    showApiErrors(it.errorTypeName)
                }
            }
        })

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    var listResponse = it.data.result
                    listResponse.forEach{ item->
                        showToast(item.message)
                        onpenDashboard(item.success)
                    }
                    //Toast.makeText(activity, ""+it.data.result, Toast.LENGTH_LONG).show()
                    Log.d("Info", "result: ${it.data} ")
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", "Error: $it.exception ")
                }
            }
        })


    }

    fun login(){
        val email = binding.userEmail.text.toString().trim()
        val password = binding.userPassword.text.toString().trim()
        viewModel.loginUser(email, password)
    }

    fun showApiErrors(errorType:String){
        if(errorType.equals("TIMEOUT")){
            showToast("No es posible conectarse al servidor.")
        }else if(errorType.equals("NETWORK")){
            showToast("No hay conexión para hacer la petición")
        }
    }

    fun showToast(msg:String){
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun onpenDashboard(success:Boolean){
        if(success){
            findNavController().navigate(R.id.action_userLoginFragment_to_userDashboardFragment)
        }
    }

}