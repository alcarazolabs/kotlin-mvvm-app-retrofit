package com.example.kotlinlaravelapirestful.ui.report

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.demo.core.Resource
import com.example.kotlinlaravelapirestful.R
import com.example.kotlinlaravelapirestful.core.UserPreferences
import com.example.kotlinlaravelapirestful.data.model.ReportsResponseObj
import com.example.kotlinlaravelapirestful.data.remote.UserDataSource
import com.example.kotlinlaravelapirestful.databinding.FragmentReportsBinding
import com.example.kotlinlaravelapirestful.presentation.ReportViewModel
import com.example.kotlinlaravelapirestful.presentation.ReportViewModelFactory
import com.example.kotlinlaravelapirestful.repository.RetrofitClient
import com.example.kotlinlaravelapirestful.repository.UserRepositoryImpl
import com.example.primerappmvvmretrofitkotlin.ui.main.adapters.ReportsAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ReportsFragment : Fragment(R.layout.fragment_reports), ReportsAdapter.OnReportClickListener {

    private lateinit var binding : FragmentReportsBinding
    protected lateinit var userPreferences: UserPreferences
    //Instanciar viewModel
    private val viewModel by viewModels<ReportViewModel> { ReportViewModelFactory(UserRepositoryImpl(
        UserDataSource(RetrofitClient.webservice), UserPreferences(requireContext())
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReportsBinding.bind(view)
        //inicializar userPreferences como se prometio con lateinit
        userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch { userPreferences.authToken.first() }

        setupObservers()
    }

    fun setupObservers(){
        viewModel.fetchReports(getAuthToken()).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    //binding.rvUsers.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                    binding.rvReports.adapter = ReportsAdapter(it.data.result, this@ReportsFragment)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE

                    Log.d("fetchReportsError", "${it.exception}")
                }
            }
        })
    }

    fun getAuthToken() : String {
        var authToken : String?
        runBlocking {
            authToken = userPreferences.authToken.first()
        }
        return authToken!!
    }

    override fun onReportClick(report: ReportsResponseObj) {
        Log.d("click","Reporte: ${report.description}")
        val action = ReportsFragmentDirections.actionReportsFragmentToDetailReportFragment(report.description, report.photo, report.created_at)
        //navegar al fragment detalle
        findNavController().navigate(action)
    }
}