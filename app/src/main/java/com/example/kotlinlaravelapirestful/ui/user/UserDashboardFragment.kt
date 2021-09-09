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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.core.UserPreferences
import com.example.kotlinlaravelapirestful.data.model.User
import com.example.kotlinlaravelapirestful.data.remote.UserDataSource
import com.example.kotlinlaravelapirestful.databinding.FragmentUserDashboardBinding
import com.example.kotlinlaravelapirestful.presentation.UserViewModel
import com.example.kotlinlaravelapirestful.presentation.UserViewModelFactory
import com.example.kotlinlaravelapirestful.repository.RetrofitClient
import com.example.kotlinlaravelapirestful.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class UserDashboardFragment : Fragment(R.layout.fragment_user_dashboard) {
    private lateinit var binding : FragmentUserDashboardBinding
    protected lateinit var userPreferences: UserPreferences

    //Instanciar viewModel
    private val viewModel by viewModels<UserViewModel> { UserViewModelFactory(UserRepositoryImpl(
        UserDataSource(RetrofitClient.webservice), UserPreferences(requireContext())
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDashboardBinding.bind(view)
        //inicializar userPreferences como se prometio con lateinit
        userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch { userPreferences.authToken.first() }

        setupObservers()
        getUserInfo()

        binding.btnLogOut.setOnClickListener {
            doLogOut()
        }

        binding.btnCreateReport.setOnClickListener {
            findNavController().navigate(R.id.action_userDashboardFragment_to_registerReportFragment)
        }
        binding.btnReports.setOnClickListener {
            findNavController().navigate(R.id.action_userDashboardFragment_to_reportsFragment)
        }
    }

    fun setupObservers(){

        viewModel.apiError.observe(viewLifecycleOwner, {
            when(it){
                is Resource.apiError->{
                    showApiErrors(it.errorTypeName)
                }
            }
        })

        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    var listResponse = it.data.result
                    listResponse.forEach { res ->
                        showUserInfo(res.user)
                    }
                }
            }
        })
    }
    //se lanza corrutina para poder obtener el token
     fun getUserInfo()  = lifecycleScope.launch{
        //obtener token
        // val authToken = runBlocking{ userPreferences.authToken.first() } same result, try it.
        val authToken = userPreferences.authToken.first() //also it works.
        Log.d("tokensito", "$authToken")
        //obtener información del usuario.
        viewModel.getUser(authToken!!)
    }

    fun showUserInfo(user: User){
        with(binding) {
            lblUserNames.text = user.name.toString()
            lblUserEmail.text = user.email.toString()
        }
    }

    fun doLogOut() = lifecycleScope.launch {
        //delete auth-token
        userPreferences.clear()
        //send user to login
         findNavController().navigate(R.id.action_userDashboardFragment_to_userLoginFragment)
    }

    fun showApiErrors(errorType:String){
        if(errorType.equals("TIMEOUT")){
            showToast("No es posible conectarse al servidor.")
        }else if(errorType.equals("NETWORK")){
            showToast("No hay conexión para hacer la petición")
        }
    }

    fun showToast(msg:String){
        Toast.makeText(activity, ""+msg, Toast.LENGTH_LONG).show()
    }

}