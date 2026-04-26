package ru.practicum.android.diploma.vacancy.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

interface VacancyDetailsRepository {
    fun getVacancyInfo(vacancyId: String): Flow<VacancyDetails?>
}
