package ru.practicum.android.diploma.chooseindustry.model

import ru.practicum.android.diploma.core.domain.models.VacancyDetails

data class RecyclerState(
    val list: List<VacancyDetails.Industry>,
    val filter: String,
    val selectItem: VacancyDetails.Industry?
)
