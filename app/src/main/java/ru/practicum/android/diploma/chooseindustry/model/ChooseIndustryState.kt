package ru.practicum.android.diploma.chooseindustry.model

import ru.practicum.android.diploma.core.domain.models.VacancyDetails

sealed class ChooseIndustryState {
    object Empty : ChooseIndustryState() // Пустой список
    object Loading : ChooseIndustryState() // Состояние загрузки
    data class Success(val items: List<VacancyDetails.Industry>) : ChooseIndustryState() // Список отраслей
    data class Error(val serverCode: String) : ChooseIndustryState() // Ошибка (интернет и т.д.)
}
