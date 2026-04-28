package ru.practicum.android.diploma.core.data.dto.vacancycard

import ru.practicum.android.diploma.core.domain.models.VacancyCardSalary

data class VacancyCardDto(
    val id: String,
    val name: String,
    val company: String?,
    val city: String?,
    val salary: VacancyCardSalary,
    val logo: String?
)
