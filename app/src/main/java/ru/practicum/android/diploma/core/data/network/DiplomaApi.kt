package ru.practicum.android.diploma.core.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap
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

    /**
     * При обычном подходе получается слишком много параметров у функции,
     * поэтому используем QueryMap
     *
     * Пример использования:
     * val filters = mapOf(
     *     "area" to 1,
     *     "industry" to null, // Retrofit автоматически пропустит null
     *     "text" to "Kotlin developer",
     *     "salary" to 150000,
     *     "page" to 0,
     *     "only_with_salary" to true
     * )
     *
     * val response = api.getVacancies("Bearer your_token", filters)
     */
    @Headers("Content-Type: application/json")
    @GET("vacancies")
    suspend fun getVacancies(
        @Header("Authorization") token: String,
        @QueryMap(encoded = true) filters: Map<String, String>
    ): Response<VacancyCardResponse>

    @Headers("Content-Type: application/json")
    @GET("vacancies/{id}")
    suspend fun getVacancy(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<VacancyDetailDto>
}

