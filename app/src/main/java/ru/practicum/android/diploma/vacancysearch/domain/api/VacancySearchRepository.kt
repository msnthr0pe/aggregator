package ru.practicum.android.diploma.vacancysearch.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse

/** Это интерфейс для связи слоя Domain со слоем Data */
interface VacancySearchRepository {
    fun vacancySearch(
        token: String,
        area: Int? = null,
        industry: Int? = null,
        text: String? = null,
        salary: Int? = null,
        page: Int? = null,
        onlyWithSalary: Boolean? = null,
    ): Flow<Result<VacancyCardResponse>>
}
