package ru.practicum.android.diploma.vacancy.model

import ru.practicum.android.diploma.core.domain.models.VacancyDetails

data class VacancyState(val vacancyDetails: VacancyDetails?, val isFavorite: Boolean)
