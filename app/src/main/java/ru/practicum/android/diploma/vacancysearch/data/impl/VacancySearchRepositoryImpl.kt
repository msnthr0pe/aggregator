package ru.practicum.android.diploma.vacancysearch.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardRequest
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

class VacancySearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacancySearchRepository {
    companion object {
        const val CODE_200 = 200
    }

    override fun vacancySearch(
        token: String,
        filters: Map<String, String>
    ): Flow<Result<VacancyCardResponse>> = flow {
        val payload = networkClient.doRequest<VacancyCardResponse>(
            VacancyCardRequest(token = token, filters = filters)
        )

        if (payload.result != null && payload.resultCode == CODE_200) {
            emit(Result.success(payload.result))
        } else {
            emit(Result.failure(Exception(payload.resultCode.toString())))
        }
    }
}
