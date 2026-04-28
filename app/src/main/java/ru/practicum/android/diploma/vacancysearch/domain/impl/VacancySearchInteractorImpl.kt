package ru.practicum.android.diploma.vacancysearch.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

class VacancySearchInteractorImpl(
    private val repository: VacancySearchRepository
) : VacancySearchInteractor {
    override fun vacancySearch(
        token: String,
        filters: Map<String, String>
    ): Flow<Result<VacancyCardResponse>> {
        return repository.vacancySearch(token = token, filters = filters)
    }
}
