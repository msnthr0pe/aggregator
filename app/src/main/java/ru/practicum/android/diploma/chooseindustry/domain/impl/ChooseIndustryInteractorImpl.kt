package ru.practicum.android.diploma.chooseindustry.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.chooseindustry.domain.api.ChooseIndustryInteractor
import ru.practicum.android.diploma.chooseindustry.domain.api.ChooseIndustryRepository
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

class ChooseIndustryInteractorImpl(
    private val repository: ChooseIndustryRepository
) : ChooseIndustryInteractor {
    override fun getIndustries(): Flow<Result<List<VacancyDetails.Industry>>> {
        return repository.getIndustries()
    }
}
