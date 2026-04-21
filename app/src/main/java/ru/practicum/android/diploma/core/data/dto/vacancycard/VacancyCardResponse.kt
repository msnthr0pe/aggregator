package ru.practicum.android.diploma.core.data.dto.vacancycard

data class VacancyCardResponse(
    val found: Int,
    val pages: Int,
    val page: Int,
    val items: List<VacancyCardDto>
)
