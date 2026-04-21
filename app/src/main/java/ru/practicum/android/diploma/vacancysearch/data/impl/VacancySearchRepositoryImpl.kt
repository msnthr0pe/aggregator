package ru.practicum.android.diploma.vacancysearch.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

class VacancySearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacancySearchRepository {
    override fun vacancySearch(
        token: String,
        area: Int?,
        industry: Int?,
        text: String?,
        salary: Int?,
        page: Int?,
        onlyWithSalary: Boolean?
    ): Flow<Result<VacancyCardResponse>> = flow {
//        val payload = networkClient.doRequest<List<AreaDto>>(AreaRequest(token = "Bearer $token"))
//        val payload = networkClient.doRequest<List<IndustryDto>>(IndustryRequest(token = "Bearer $token"))

        // Тестовый запрос ()
//        val payload = networkClient.doRequest<VacancyCardResponse>(VacancyCardRequest(
//            token = "Bearer $token",
//            area = area,
//            industry = industry,
//            text = text,
//            salary = salary,
//            page = page,
//            onlyWithSalary = onlyWithSalary
//        ))

//        val payload = networkClient.doRequest<List<VacancyDetailDto>>(VacancyDetailRequest(
//            token = "Bearer $token",
//            id = "123321"
//        ))
//
//        if(payload.result != null) {
//            Log.i("TEEEST123", payload.result.toString())
//            Log.i("TEEEST123", payload.resultCode.toString())
//        }
    }
}
