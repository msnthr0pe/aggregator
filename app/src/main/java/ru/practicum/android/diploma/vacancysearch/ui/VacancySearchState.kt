package ru.practicum.android.diploma.vacancysearch.ui

import ru.practicum.android.diploma.core.domain.models.VacancyCard

data class VacancySearchState(
    val query: String = "",
    val vacancyList: List<VacancyCard> = emptyList()
)

// Поля фильтрации
// Место работы (страна): label, value
// Место работы (регион): label, value
// Отрасль: label, value
// Ожидаемая ЗП: value
// Не показывать без зарплаты: bool
