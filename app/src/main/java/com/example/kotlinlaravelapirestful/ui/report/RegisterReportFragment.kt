package com.example.kotlinlaravelapirestful.ui.report

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.core.UploadRequestBody
import com.example.kotlinlaravelapirestful.core.UserPreferences
import com.example.kotlinlaravelapirestful.data.remote.UserDataSource
import com.example.kotlinlaravelapirestful.databinding.FragmentRegisterReportBinding
import com.example.kotlinlaravelapirestful.presentation.ReportViewModel
import com.example.kotlinlaravelapirestful.presentation.ReportViewModelFactory
import com.example.kotlinlaravelapirestful.repository.RetrofitClient
import com.example.kotlinlaravelapirestful.repository.UserRepositoryImpl
import kotlinx.android.synthetic.main.fragment_register_report.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterReportFragment : Fragment(R.layout.fragment_register_report), UploadRequestBody.UploadCallback {

    private lateinit var binding : FragmentRegisterReportBinding
    protected lateinit var userPreferences: UserPreferences

    //variable para manejar la imagen
    private var selectedImageUri: Uri? = null

    //Instanciar viewModel
    private val viewModel by viewModels<ReportViewModel> { ReportViewModelFactory(UserRepositoryImpl(
        UserDataSource(RetrofitClient.webservice), UserPreferences(requireContext())
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterReportBinding.bind(view)
        //inicializar userPreferences como se prometio con lateinit
        userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch { userPreferences.authToken.first() }

        binding.imageReport.setOnClickListener {
            openImageChooser()
        }
        binding.btnRegister.setOnClickListener {
            registerReport()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_registerReportFragment_to_userDashboardFragment)
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
        viewModel.registerReport.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressBarHorizontal.progress=0
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    var listResponse = it.data.result

                    listResponse.forEach{ item->
                        showToast(item.message)
                        if(!item.success){
                            showToast(item.error!!)
                        }else{
                            findNavController().navigate(R.id.action_registerReportFragment_to_userDashboardFragment)
                        }

                    }

                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressBarHorizontal.progress = 0
                    Log.d("Error", "Error: $it.exception ")
                }
            }
        })
    }
    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    imageReport.setImageURI(selectedImageUri)
                }
            }
        }
    }
    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
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

    //@RequiresApi(Build.VERSION_CODES.KITKAT)
     fun registerReport() {
        if (selectedImageUri == null || binding.txtDescription.text.toString()=="") {
            showToast("Una imágen es requerida/descripción")
            return
        }
        /*
        * A continuación se toma la imágen elegida por el usuario y guardar dentro del cache de la aplicación
        * eso se debe de hacer desde Android 10. No se debe de hacer una copia de la imagen si guardarla la cache.
        *
        * La idea es hacer una copia del archivo que el usuario elige en la galería de medios y
        *  agregarlo al directorio de caché de su aplicación.
        * Fuente: https://medium.com/@sriramaripirala/android-10-open-failed-eacces-permission-denied-da8b630a89df
        * */
        val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(selectedImageUri!!, "r") ?: return
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor

        parcelFileDescriptor?.let {
            //Crear inputStream
            val inputStream = FileInputStream(fileDescriptor)
            //Crear un archivo dentro del directorio cache de la aplicación
            val file = File(context?.cacheDir, context?.contentResolver?.getFileName(selectedImageUri!!))
            //Ahora hagamos OutputStream a partir de este archivo
            val outputStream = FileOutputStream(file)
            //El paso final es copiar el contenido del archivo original a nuestro archivo recién creado en nuestro directorio de caché.
            IOUtils.copy(inputStream, outputStream)
            //para usar IOUtils se implemento la dependencia org.apache.commons.io.IOUtils

            //registrar el reporte
            //1. Obtener el token
            val authToken = getAuthToken()
            //2. Llamar método que registra el reporte y pasarle la data
            val description = binding.txtDescription.text.toString()
            //parsear string a requestBody, asi lo requiere el Api - ver el método en WebService.kt
            val description_reqbody = description.toRequestBody("text/plain;charset=utf-8".toMediaType())
            //3. parsear file a requestBody luego a MultipartBody Part
            var imagePart: MultipartBody.Part? = null
            val fileReqBody = file.asRequestBody("image/*".toMediaType())
            //Instanciar clase helper que ayuda actualizar el progress bar horizontal
            val body = UploadRequestBody(file, "image", this)
            imagePart = MultipartBody.Part.createFormData("photo", file.name, body)
            //El parámetro "body" de MultipartBody.Part.createFormData(..,body) es la variable de la clase  UploadRequestBody
            // instanciada, se le pasa para poder actualizar el progressbar horizontal, si no deseamos hacer eso en su lugar
            // se debe de pasar la variable fileReqBody en lugar de body.

            //Registrar el reporte llamado al método del ViewModel

            viewModel.registerReport(authToken!!,description_reqbody,imagePart)
        }
    }
    //helper para poder obtener el nombre del archivo a partir de la Uri
    fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

     fun getAuthToken() : String {
        var authToken : String?
         runBlocking {
             authToken = userPreferences.authToken.first()
         }
        return authToken!!
    }
    //Método sobre escrito de UploadRequestBody.UploadCallback. Sirve para actualizar el
    //porcentaje del progress bar.
    override fun onProgressUpdate(percentage: Int) {
            binding.progressBarHorizontal.progress = percentage
    }
}