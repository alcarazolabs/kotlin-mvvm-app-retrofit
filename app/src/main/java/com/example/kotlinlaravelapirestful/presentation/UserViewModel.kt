package com.example.kotlinlaravelapirestful.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.core.ApiCallsHandler
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//########### CURRENT GITHUB BRANCH: safeApiCall
//Details:
/*
* Handle of errors and the app prevents get crash. We use safeApiCall method.
* Credits: Emmanuel Guerra (https://dev.to/eagskunst/making-safe-api-calls-with-retrofit-and-coroutines-1121)
*  - Features of this branch:
* - The user can register his-selft
* */


//mediante livedata se le da la data a la vista
class UserViewModel (private var repo : UserRepository) : ViewModel(), ApiCallsHandler.RemoteErrorEmitter {


    private val _registerResponse: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegisterResponse>>
        get() = _registerResponse

    //MutableLiveData for set  Error Type of the Api when happen and error from ApiCallsHandler
    private val _apiError: MutableLiveData<Resource<String>> = MutableLiveData()
    val apiError: LiveData<Resource<String>> //we need to subscribe to this variable in the fragment/activity
        get() = _apiError
    //this function will throw the apiError, we use it in line 60..
    fun setApiError(errorType:String) = viewModelScope.launch {
        _apiError.value = Resource.apiError(errorType)
    }

    fun registerUser(
        name: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _registerResponse.value = Resource.Loading()
        _registerResponse.value = ApiCallsHandler.safeApiCall(this@UserViewModel){
           Resource.Success(repo.registerUser(name, email, password))
       }



    }

    override fun onError(msg: String) {
        Log.d("Error", msg)
    }

    override fun onError(errorType: ApiCallsHandler.ErrorType) {
        Log.d("Error", "errorType: $errorType")
        setApiError(errorType.toString())
    }




}

//Video mínuto 21:35 Video 54: Capa de Presentación
//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/23911968#overview
//Desde la vista se le pasara la clase que implementa los métodos del repositorio
class UserViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UserRepository::class.java).newInstance(repo)
    }
}
