package ru.practicum.android.diploma.vacancy.model

sealed class VacancyPageState {
    object Loading : VacancyPageState()
    data class Success(val vacancyState: VacancyState) : VacancyPageState()
    data class Error(val serverCode: String) : VacancyPageState()
}
