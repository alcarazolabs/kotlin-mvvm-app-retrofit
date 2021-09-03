package com.example.kotlinlaravelapirestful.presentation

import androidx.lifecycle.*
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.repository.UserRepository
import kotlinx.coroutines.launch

//########### CURRENT GITHUB BRANCH: main
//Details:
/*
* In this branch there is not handle of exceptions when te server is off or the user has wifi off
* By the moment the app get crash when lack wifi or the server is off.
* The next branch I'm going to implement "safeApiCall"
* - Features of this branch:
* - The user can register his-selft
* */

//mediante livedata se le da la data a la vista
class UserViewModel (private var repo : UserRepository) : ViewModel() {

    private val _registerResponse: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegisterResponse>>
        get() = _registerResponse

    fun registerUser(
        name: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _registerResponse.value = Resource.Loading()
       _registerResponse.value = Resource.Success(repo.registerUser(name, email, password))


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
