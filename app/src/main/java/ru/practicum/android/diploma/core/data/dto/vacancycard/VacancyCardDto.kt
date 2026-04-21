package ru.practicum.android.diploma.core.data.dto.vacancycard

data class VacancyCardDto(
    val id: String,
    val name: String,
    val company: String?,
    val city: String?,
    val salary: VacancyCardSalaryDto,
    val logo: String?
) {
    data class VacancyCardSalaryDto(
        val from: Int?,
        val to: Int?,
        val currency: String?
    )
}
