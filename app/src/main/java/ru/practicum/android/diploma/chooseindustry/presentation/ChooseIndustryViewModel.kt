package ru.practicum.android.diploma.chooseindustry.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.chooseindustry.model.ChooseIndustryState

class ChooseIndustryViewModel(): ViewModel() {
    private val pageLiveData = MutableLiveData<ChooseIndustryState>()
    fun observePage(): LiveData<ChooseIndustryState> = pageLiveData


    /** Загрузка списка отраслей */
    fun loadIndustries() {
        viewModelScope.launch {}
    }
}
