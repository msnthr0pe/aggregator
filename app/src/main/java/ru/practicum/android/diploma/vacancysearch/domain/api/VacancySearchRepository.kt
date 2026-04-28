package ru.practicum.android.diploma.vacancysearch.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse

/** Это интерфейс для связи слоя Domain со слоем Data */
interface VacancySearchRepository {
    fun vacancySearch(
        token: String,
        filters: Map<String, String>
    ): Flow<Result<VacancyCardResponse>>
}
