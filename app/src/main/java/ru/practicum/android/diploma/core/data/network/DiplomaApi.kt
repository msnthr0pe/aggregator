package ru.practicum.android.diploma.core.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.core.data.dto.area.AreaDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardResponse
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto

/**
 * API приложения
 *
 * В этом интерфейсе возвращается Response от retrofit2 (внутри data class с Response приложения)
 * Это необходимо для ля корректной работы функции handle в RetrofitNetworkClient
 * */
interface DiplomaApi {
    @Headers("Content-Type: application/json")
    @GET("areas")
    suspend fun getAreas(
        @Header("Authorization") token: String
    ): Response<List<AreaDto>>

    @Headers("Content-Type: application/json")
    @GET("industries")
    suspend fun getIndustries(
        @Header("Authorization") token: String
    ): Response<List<IndustryDto>>

    @Headers("Content-Type: application/json")
    @GET("vacancies")
    suspend fun getVacancies(
        @Header("Authorization") token: String,
        @Query("area") area: Int?,
        @Query("industry") industry: Int?,
        @Query("text") text: String?,
        @Query("salary") salary: Int?,
        @Query("page") page: Int?,
        @Query("only_with_salary") onlyWithSalary: Boolean?,
    ): Response<VacancyCardResponse>

    @Headers("Content-Type: application/json")
    @GET("vacancies/{id}")
    suspend fun getVacancy(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<VacancyDetailDto>
}

