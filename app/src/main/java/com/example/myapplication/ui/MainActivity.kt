package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.models.FacilityToShow
import com.example.myapplication.repository.PropertyRepository
import com.example.myapplication.repository.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var options : ArrayList<String>
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var viewModelGlobal : MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val repository = PropertyRepository(RetrofitClient.getClient)
        val factory = PropertyViewModelFactory(repository)
        val viewModel : MainActivityViewModel by viewModels { factory }
        viewModelGlobal = viewModel
        setupSpinner()
        if(!viewModel.getLoadingStatus.value!!) {
            val facilityToShow = viewModel.getFacilityToShow()
            makeProgressBarGone()
            showThisFacility(facilityToShow)

        }
        else{
            makeProgressBarVisible()

        }
        binding.btnNext.setOnClickListener {
            viewModel.onOptionSelected(binding.spnOptions.selectedItem.toString())
        }

        binding.btnRestart.setOnClickListener {
            viewModel.restart()
        }
        viewModel.getState.observe(this,  {
            if(!viewModel.getLoadingStatus.value!!) {
                val facilityToShowNext = viewModel.getFacilityToShow()
                showThisFacility(facilityToShowNext)
            }
        })
        viewModel.getLoadingStatus.observe(this, {
            if(!it){
                val facilityToShowNext = viewModel.getFacilityToShow()
                makeProgressBarGone()
                showThisFacility(facilityToShowNext)

            }
        })

    }
    private fun setupSpinner(){
        options = arrayListOf()
        adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,options)
        binding.spnOptions.adapter = adapter
    }

    private fun showThisFacility(facilityToShow: FacilityToShow){
        //binding.btnNext.isEnabled = false

        if(facilityToShow.facility == null){
            var stringToShow = ""
            Log.d("TAGAniket", facilityToShow.options.toString())
            for(op in facilityToShow.options){
                stringToShow += op
                stringToShow+= "\n\n"
            }
            binding.apply {
                spnOptions.visibility = View.GONE
                btnNext.visibility = View.GONE
                tvDetail.text = "Options that you have selected"
                tvFacility.text = stringToShow
                btnRestart.visibility = View.VISIBLE
            }

        }
        else{
            binding.apply {
                spnOptions.visibility = View.VISIBLE
                btnNext.visibility = View.VISIBLE
                tvDetail.text = "Select your Preferences"
                tvFacility.text = facilityToShow.facility
                btnRestart.visibility = View.GONE
            }
            options.clear()
            for(op in facilityToShow.options) {
                options.add(op)
            }
            adapter.notifyDataSetChanged()
        }
        binding.spnOptions.setSelection(0)
    }

    private fun makeProgressBarVisible(){
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvFacility.visibility = View.GONE
            tvDetail.visibility = View.GONE
            btnNext.visibility = View.GONE
            btnRestart.visibility = View.GONE
            spnOptions.visibility = View.GONE
        }
    }
    private fun makeProgressBarGone(){
        binding.apply {
            progressBar.visibility = View.GONE
            tvFacility.visibility = View.VISIBLE
            tvDetail.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
            spnOptions.visibility = View.VISIBLE
        }
    }


    override fun onBackPressed() {
        if(viewModelGlobal.getState.value == 0){
            super.onBackPressed()
        }
        else{
            viewModelGlobal.onBackPressed()
        }
    }
}