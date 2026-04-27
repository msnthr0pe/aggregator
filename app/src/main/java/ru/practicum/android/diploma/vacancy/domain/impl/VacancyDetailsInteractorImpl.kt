package ru.practicum.android.diploma.vacancy.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsRepository

class VacancyDetailsInteractorImpl(
    private val repository: VacancyDetailsRepository
) : VacancyDetailsInteractor {
    override fun getVacancyInfo(vacancyId: String): Flow<VacancyDetails?> =
        repository.getVacancyInfo(vacancyId)
}
