package com.example.myapplication.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.ApiResponse
import com.example.myapplication.models.Facility
import com.example.myapplication.models.FacilityToShow
import com.example.myapplication.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivityViewModel(private val repository: PropertyRepository) : ViewModel() {
    private var state = MutableLiveData<Int>()
    val getState : LiveData<Int>
    get() = state

    private var isLoadingFromApi = MutableLiveData<Boolean>()
    val getLoadingStatus : LiveData<Boolean>
    get() = isLoadingFromApi


    private val allFacilities = ArrayList<Facility>()
    private val optionsIdSelected = mutableMapOf<Int,Boolean>()
    private val getOptionIdFromName = mutableMapOf<String,Int>()
    private val optionsSelected = ArrayList<String>()
    private val wontGoIfTheseOptionsAreSelected = mutableMapOf<Int,ArrayList<Int>>()

    /*
        0 means user need to select facility 1
        1 -> 2
        2 -> 3
        4 -> on last page
     */
    init {
        state.value = 0
        isLoadingFromApi.value = true
        allFacilities.clear()
        loadDataFromApi()

    }

    fun onOptionSelected(option : String){
        optionsIdSelected[getOptionIdFromName[option]!!] = true
        optionsSelected.add(option);
        state.value = state.value!! + 1
    }

    fun getFacilityToShow() : FacilityToShow{
        if(state.value == 3){
            return FacilityToShow(optionsSelected,null)
        }
        else{
            val options = ArrayList<String>()
            for(op in allFacilities[state.value!!].options){
                var willGo = true
                if(wontGoIfTheseOptionsAreSelected[op.id.toInt()] != null) {
                    for (item in wontGoIfTheseOptionsAreSelected[op.id.toInt()]!!) {
                        if (optionsIdSelected.getOrDefault(item,false)) {
                            willGo = false
                            break
                        }
                    }
                }
                if(willGo){
                    options.add(op.name)
                }
            }
            return FacilityToShow(options,allFacilities[state.value!!].name)
        }
    }


    private fun loadDataFromApi(){
        GlobalScope.launch(Dispatchers.IO) {

            val res  = repository.getFacilitiesAndExclusions()
            if(res.isSuccessful){
                for(facility in res.body()!!.facilities){
                    allFacilities.add(facility)
                    for(option in facility.options){
                        getOptionIdFromName[option.name] = option.id.toInt()
                    }
                }
                for(pairExclusion in res.body()!!.exclusions){
                    if(wontGoIfTheseOptionsAreSelected[pairExclusion[1].optionsId.toInt()] == null){
                        wontGoIfTheseOptionsAreSelected[pairExclusion[1].optionsId.toInt()] = arrayListOf()
                    }
                    wontGoIfTheseOptionsAreSelected[pairExclusion[1].optionsId.toInt()]!!.add(pairExclusion[0].optionsId.toInt())
                }
                withContext(Dispatchers.Main){
                    isLoadingFromApi.value = false
                }


            }
        }
    }

    fun restart(){
        optionsIdSelected.clear()
        optionsSelected.clear()
        state.value = 0
    }

    private fun goToPreviousFacility(){
        optionsIdSelected[getOptionIdFromName[optionsSelected.last()]!!.toInt()] = false
        optionsSelected.removeLast()
        state.value = state.value!! - 1
    }

    fun onBackPressed(){
        goToPreviousFacility()
    }

}