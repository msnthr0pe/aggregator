package ru.practicum.android.diploma.chooseindustry.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.chooseindustry.domain.api.ChooseIndustryRepository
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryRequest
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.core.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.util.getToken

class ChooseIndustryRepositoryImpl(
    private val networkClient: NetworkClient,
) : ChooseIndustryRepository {
    override fun getIndustries(): Flow<Result<List<VacancyDetails.Industry>>> = flow {
        val response = networkClient.doRequest<List<IndustryDto>>(IndustryRequest(getToken()))
        val resultCode = response.resultCode
        val result = response.result

        if (resultCode == RetrofitNetworkClient.CODE_200 && result != null) {
            emit(Result.success(result.map { VacancyDetails.Industry(id = it.id, name = it.name) }))
        } else {
            emit(Result.failure(Exception(resultCode.toString())))
        }
    }
}
