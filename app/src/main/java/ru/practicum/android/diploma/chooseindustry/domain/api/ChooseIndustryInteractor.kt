package ru.practicum.android.diploma.chooseindustry.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

interface ChooseIndustryInteractor {
    fun getIndustries(): Flow<Result<List<VacancyDetails.Industry>>>
}
