package ru.practicum.android.diploma.vacancysearch.ui.state

sealed class VacancySearchState {
    object Nothing : VacancySearchState() // Первое открытие
    object Empty : VacancySearchState() // Пустой список
    object Loading : VacancySearchState() // Состояние загрузки
    object Success : VacancySearchState() // Список вакансий
    data class Error(val serverCode: String) : VacancySearchState() // Ошибка (интернет и т.д.)
}
