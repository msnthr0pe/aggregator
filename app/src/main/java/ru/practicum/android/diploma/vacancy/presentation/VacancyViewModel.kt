package ru.practicum.android.diploma.vacancy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.vacancy.model.VacancyState

class VacancyViewModel(
    private val interactor: VacancyDetailsInteractor
) : ViewModel() {

    private var vacancyDetails: VacancyDetails? = null
    private var isServerError: Boolean = false
    private var vacancyState = VacancyState(
        vacancyDetails = vacancyDetails,
        isServerError = isServerError,
    )
    private val _vacancyStateLiveData = MutableLiveData(vacancyState)
    fun observeVacancyDetails(): LiveData<VacancyState> = _vacancyStateLiveData
    fun requestVacancyDetails(vacancyId: String) {
        viewModelScope.launch {
            interactor
                .getVacancyInfo(vacancyId)
                .collect {
                    vacancyDetails = it
                    isServerError = it == null
                    postState()
                }
        }
    }

    private fun postState() {
        vacancyState = VacancyState(
            vacancyDetails = vacancyDetails,
            isServerError = isServerError,
        )
        _vacancyStateLiveData.postValue(vacancyState)
    }
}
