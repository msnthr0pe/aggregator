package ru.practicum.android.diploma.vacancysearch.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor

class VacancySearchViewModels(
    private val vacancySearchInteractor: VacancySearchInteractor
) : ViewModel() {

    /** Тестовый метод для поиска вакансий */
    fun searchVacancy() {
        viewModelScope.launch {
            vacancySearchInteractor.vacancySearch(
                token = BuildConfig.API_ACCESS_TOKEN,
                text = "android"
            ).collect { result ->
                Log.i("RESULT", result.toString())
            }
        }
    }
}
