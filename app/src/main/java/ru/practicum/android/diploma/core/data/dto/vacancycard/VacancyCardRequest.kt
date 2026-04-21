package ru.practicum.android.diploma.core.data.dto.vacancycard

data class VacancyCardRequest(
    val token: String,
    val area: Int?,
    val industry: Int?,
    val text: String?,
    val salary: Int?,
    val page: Int?,
    val onlyWithSalary: Boolean?
)
