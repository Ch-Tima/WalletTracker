package com.chtima.wallettracker.viewModels

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chtima.wallettracker.fragments.dialogs.FilterDialogFragment

class TransactionReportViewModel(var app: Application): AndroidViewModel(app){

    private var filterParamsLiveData: MutableLiveData<FilterDialogFragment.FilterParams> = MutableLiveData<FilterDialogFragment.FilterParams>()

    init {
        filterParamsLiveData.value = FilterDialogFragment.FilterParams()
    }


    fun setFilterParams(params: FilterDialogFragment.FilterParams?) {
        if(params == null){
            filterParamsLiveData = MutableLiveData<FilterDialogFragment.FilterParams>();
        }else{
            this.filterParamsLiveData.value = params
        }

    }

    fun getFilter(): FilterDialogFragment.FilterParams? {
        return this.filterParamsLiveData.value
    }


}