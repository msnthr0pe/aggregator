package ru.practicum.android.diploma.core.data.dto.vacancycard

import ru.practicum.android.diploma.core.domain.models.VacancyCard

data class VacancyCardResponse(
    val found: Int,
    val pages: Int,
    val page: Int,
    val items: List<VacancyCard>
)
