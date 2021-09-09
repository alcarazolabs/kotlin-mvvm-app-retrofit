package com.example.kotlinlaravelapirestful.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.core.ApiCallsHandler
import com.example.kotlinlaravelapirestful.data.model.RegisterResponse
import com.example.kotlinlaravelapirestful.data.model.UserInfoResponse
import com.example.kotlinlaravelapirestful.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ReportViewModel  (private var repo : UserRepository) : ViewModel(), ApiCallsHandler.RemoteErrorEmitter {

    //MutableLiveData for set  Error Type of the Api when happen and error from ApiCallsHandler
    private val _apiError: MutableLiveData<Resource<String>> = MutableLiveData()
    val apiError: LiveData<Resource<String>> //we need to subscribe to this variable in the fragment/activity
        get() = _apiError
    //this function will throw the apiError, we use it in line 60..
    fun setApiError(errorType:String) = viewModelScope.launch {
        _apiError.value = Resource.apiError(errorType)
    }

    //################################## LiveData para registrar el reporte del usuario ######

    private val _registerReport: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val registerReport: LiveData<Resource<RegisterResponse>>
        get() = _registerReport

    fun registerReport(access_token: String, description: RequestBody, photo: MultipartBody.Part) = viewModelScope.launch {
        _registerReport.value = Resource.Loading()
        _registerReport.value = ApiCallsHandler.safeApiCall(this@ReportViewModel) {
            Resource.Success(repo.registerReport(access_token, description, photo))
        }
    }
    //##########################################################################################

    //####################### LiveData para Obtener todos los reportes #########################
    fun fetchReports(access_token: String) = liveData(Dispatchers.IO) {
        //Emitir primer estado 'carga'
        emit(Resource.Loading())
        try {
            //Emitir segundo estado 'Exitoso'
             var success =  ApiCallsHandler.safeApiCall(this@ReportViewModel) {
                    Resource.Success(repo.getReports(access_token))
                    }
           emit(success)
        } catch (e: Exception) {
            //Tercer estado Error/Excepcion
            emit(Resource.Failure(e))
        }
    }
    //##########################################################################################

    //###### implemented methods from ApiCallsHandler.RemoteErrorEmitter #######################
    override fun onError(msg: String) {
        Log.d("Error", msg)
    }

    override fun onError(errorType: ApiCallsHandler.ErrorType) {
        Log.d("Error", "errorType: $errorType")
        setApiError(errorType.toString())
    }
    //##########################################################################################


}

class ReportViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UserRepository::class.java).newInstance(repo)
    }
}
