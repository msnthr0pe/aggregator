package ru.practicum.android.diploma.vacancysearch.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse

/** Это интерфейс, с помощью которого слой Presentation будет общаться со слоем Domain. */
interface VacancySearchInteractor {
    fun vacancySearch(
        token: String,
        filters: Map<String, String>
    ): Flow<Result<VacancyCardResponse>>
}
