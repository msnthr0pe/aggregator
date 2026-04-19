package ru.practicum.android.diploma.core.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.core.data.dto.AreaDto
import ru.practicum.android.diploma.core.data.dto.IndustryDto
import ru.practicum.android.diploma.core.data.dto.VacancyCardResponse
import ru.practicum.android.diploma.core.data.dto.VacancyDetailDto

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
        @Query("area") area: Int? = null,
        @Query("industry") industry: Int? = null,
        @Query("text") text: String? = null,
        @Query("salary") salary: Int? = null,
        @Query("page") page: Int? = null,
        @Query("only_with_salary") onlyWithSalary: Boolean? = null,
    ): Response<VacancyCardResponse>

    @Headers("Content-Type: application/json")
    @GET("vacancies/{id}")
    suspend fun getVacancy(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<VacancyDetailDto>
}

