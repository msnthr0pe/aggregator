package ru.practicum.android.diploma.core.data.dto.vacancycard

data class VacancyCardRequest(
    val token: String,
    val filters: Map<String, String>
)
