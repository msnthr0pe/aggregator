package ru.practicum.android.diploma.vacancysearch.ui.state

import ru.practicum.android.diploma.core.domain.models.VacancyCard

sealed class VacancySearchState {
    object Nothing : VacancySearchState() // Первое открытие
    object Empty : VacancySearchState() // Пустой список
    object Loading: VacancySearchState() // Состояние загрузки
    data class Error(val serverCode: String): VacancySearchState() // Ошибка (интернет и т.д.)
    data class Success(val data: List<VacancyCard>) : VacancySearchState() // Список вакансий
}
