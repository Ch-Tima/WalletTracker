package com.chtima.wallettracker.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chtima.wallettracker.fragments.dialogs.FilterDialogFragment

/**
 *  TransactionReportViewModel
 * Был создан для сохронения filterParams вовремя перехода между Fragments
 *
 * Управление состоянием фильтров в диалоге FilterDialogFragment
 *
 * @property app Контекст приложения, необходимый для AndroidViewModel
* */
class TransactionReportViewModel(var app: Application): AndroidViewModel(app){

    private var _filterParams: MutableLiveData<FilterDialogFragment.FilterParams> = MutableLiveData<FilterDialogFragment.FilterParams>()
    val filterParams: LiveData<FilterDialogFragment.FilterParams> = _filterParams
    init {
        _filterParams.value = FilterDialogFragment.FilterParams()
    }

    fun setFilterParams(params: FilterDialogFragment.FilterParams?) {
        _filterParams.value = params ?: FilterDialogFragment.FilterParams()
    }

    fun clear(){
        _filterParams.value = FilterDialogFragment.FilterParams()
    }


}