package com.example.kotlinlaravelapirestful.ui.user

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
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
import com.example.kotlinlaravelapirestful.databinding.FragmentUserLoginBinding
import com.example.kotlinlaravelapirestful.presentation.UserViewModel
import com.example.kotlinlaravelapirestful.presentation.UserViewModelFactory
import com.example.kotlinlaravelapirestful.repository.RetrofitClient
import com.example.kotlinlaravelapirestful.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class UserLoginFragment : Fragment(R.layout.fragment_user_login) {
    private lateinit var binding : FragmentUserLoginBinding
   protected lateinit var userPreferences: UserPreferences

    //Instanciar viewModel
    private val viewModel by viewModels<UserViewModel> { UserViewModelFactory(UserRepositoryImpl(
        UserDataSource(RetrofitClient.webservice), UserPreferences(requireContext())
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserLoginBinding.bind(view)
        //instancia clase sherePreferences
        userPreferences = UserPreferences(requireContext())
        //obtener token
        lifecycleScope.launch { userPreferences.authToken.first() }

        lifecycleScope.launch {
            val authToken = userPreferences.authToken.first()
            //si el token no es nulo enviar al usuario al dashboard
            authToken?.let {
                findNavController().navigate(R.id.action_userLoginFragment_to_userDashboardFragment)
            }
        }

        binding.btnSingUp.setOnClickListener{
            findNavController().navigate(R.id.action_userLoginFragment_to_userRegisterFragment)
        }

        binding.btnLogin.setOnClickListener{
            deleteOldAuthToken()
            doLogin()
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
                    listResponse.forEach{ res->
                        showToast(res.message)
                        Log.d("token", "${res.access_token}")
                        onpenDashboard(res.success, res.user, res.access_token)

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

    fun doLogin(){
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
        Toast.makeText(activity, ""+msg, Toast.LENGTH_LONG).show()
    }

     fun onpenDashboard(success:Boolean, user:User?, access_token:String?){
        if(success){
           Log.d("user", ""+user?.name.toString())
            //Guardar token en el sharePreferences dentro de una corrutina.. si no se hace lyfecycleScope.launch dara error xq la funcion saveAuthToken es una funcion suspendida
            lifecycleScope.launch {

                viewModel.saveAuthToken(access_token!!)

            }
            //Agregar un delay de 3 mientras el token se guardo (Opcional).
            Thread.sleep(3_000)
            //abrir fragment Dashboard
            findNavController().navigate(R.id.go_home)
            //Revisar el main_graph se agrego un action para prevenir que desde el dashboard regrese con back button, en su lugar se cierra el app, para eso se agrego el codigo en el main_graph
        }
    }


   fun deleteOldAuthToken() = lifecycleScope.launch {
       //eliminar token del datastore
       userPreferences.clear()

   }


}