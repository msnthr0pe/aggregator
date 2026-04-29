package ru.practicum.android.diploma.vacancy.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailRequest
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.core.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.util.getToken
import ru.practicum.android.diploma.core.util.toDomain
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsRepository

class VacancyDetailsRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacancyDetailsRepository {
    override fun getVacancyInfo(vacancyId: String): Flow<Result<VacancyDetails>> = flow {
        val response = networkClient.doRequest<VacancyDetailDto>(
            VacancyDetailRequest(token = getToken(), id = vacancyId)
        )

        val result = response.result
        if (response.resultCode == RetrofitNetworkClient.CODE_200 && result != null) {
            val info = with(result) {
                VacancyDetails(
                    id = id,
                    name = name,
                    description = description,
                    salary = salary,
                    address = address?.toDomain(),
                    experience = experience?.toDomain(),
                    schedule = schedule?.toDomain(),
                    employment = employment?.toDomain(),
                    contacts = contacts?.toDomain(),
                    employer = employer.toDomain(),
                    area = area.toDomain(),
                    skills = skills,
                    url = url,
                    industry = industry.toDomain(),
                )
            }
            emit(Result.success(info))
        } else {
            emit(Result.failure(Exception(response.resultCode.toString())))
        }
    }
}
