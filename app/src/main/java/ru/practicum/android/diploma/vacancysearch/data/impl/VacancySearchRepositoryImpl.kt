package ru.practicum.android.diploma.vacancysearch.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardDto
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardRequest
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

class VacancySearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacancySearchRepository {
    override fun vacancySearch(
        token: String,
        filters: Map<String, String>
    ): Flow<Result<VacancyCardResponse>> = flow {
        val payload = networkClient.doRequest<VacancyCardResponse>(
            VacancyCardRequest(token = token, filters = filters)
        )

        if (payload.result != null && payload.resultCode == 200) {
//            val convertVal = VacancyCardResponse(
//                found = payload.result.found,
//                pages = payload.result.pages,
//                page = payload.result.page,
//                items = payload.result.items
//            )
            emit(Result.success(payload.result))
        } else {
            emit(Result.failure(Exception(payload.resultCode.toString())))
        }
    }

    private fun convertDtoInVacancyCard(card: VacancyCardDto): VacancyCard {
        return VacancyCard(
            id = card.id,
            name = card.name,
            company = card.company,
            city = card.city,
            salary = card.salary,
            logo = card.logo
        )
    }
}
