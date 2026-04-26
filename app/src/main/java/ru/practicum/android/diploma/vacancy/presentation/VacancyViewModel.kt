package ru.practicum.android.diploma.vacancy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsInteractor

class VacancyViewModel(
    private val interactor: VacancyDetailsInteractor
) : ViewModel() {

    private var vacancyDetails: VacancyDetails? = null
    private val _vacancyLiveData = MutableLiveData<VacancyDetails?>(vacancyDetails)
    fun observeVacancyDetails(): LiveData<VacancyDetails?> = _vacancyLiveData
    fun requestVacancyDetails(vacancyId: String) {
        viewModelScope.launch {
            interactor
                .getVacancyInfo(vacancyId)
                .collect {
                    vacancyDetails = it
                    postState()
                }
        }
    }

    private fun postState() {
        _vacancyLiveData.postValue(vacancyDetails)
    }
}
