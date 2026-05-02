package ru.practicum.android.diploma.chooseindustry.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.chooseindustry.domain.api.ChooseIndustryInteractor
import ru.practicum.android.diploma.chooseindustry.model.ChooseIndustryState
import ru.practicum.android.diploma.chooseindustry.model.RecyclerState
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

class ChooseIndustryViewModel(
    private val interactor: ChooseIndustryInteractor,
): ViewModel() {

    private var recyclerState = RecyclerState(list = listOf(), filter = "", selectItem = null)
    private val pageLiveData = MutableLiveData<ChooseIndustryState>(ChooseIndustryState.Loading)
    fun observePage(): LiveData<ChooseIndustryState> = pageLiveData

    fun getSelectItem(): VacancyDetails.Industry? {
        return recyclerState.selectItem
    }

    fun selectIndustry(selectItem: VacancyDetails.Industry) {
        recyclerState = recyclerState.copy(selectItem = selectItem)
        pageLiveData.postValue(ChooseIndustryState.Success(recyclerState))
    }

    fun onSearch(filter: String) {
        recyclerState = recyclerState.copy(filter = filter)
        pageLiveData.postValue(ChooseIndustryState.Success(recyclerState))
    }

    /** Загрузка списка отраслей */
    fun loadIndustries() {
        viewModelScope.launch {
            interactor.getIndustries().collect { result ->
                result.onSuccess {
                    recyclerState = recyclerState.copy(list = it)
                    pageLiveData.postValue(ChooseIndustryState.Success(recyclerState))
                }

                result.onFailure {
                    pageLiveData.postValue(ChooseIndustryState.Error(it.message.toString()))
                }
            }
        }
    }
}
