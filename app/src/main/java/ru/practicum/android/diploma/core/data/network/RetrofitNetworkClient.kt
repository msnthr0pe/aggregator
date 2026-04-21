package ru.practicum.android.diploma.core.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.core.data.dto.Response
import ru.practicum.android.diploma.core.data.dto.area.AreaRequest
import ru.practicum.android.diploma.core.data.dto.industry.IndustryRequest
import ru.practicum.android.diploma.core.data.dto.vacancycard.VacancyCardRequest
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailRequest

class RetrofitNetworkClient(
    private val diplomaApi: DiplomaApi,
    private val context: Context
) : NetworkClient {
    companion object {
        const val CODE_400 = 400
        const val CODE_500 = 500
    }

    override suspend fun <T>doRequest(dto: Any): Response<T> {
        if (!isConnected()) {
            return Response<T>().apply { resultCode = -1 }
        }

        return when (dto) {
            is VacancyCardRequest -> {
                handle { diplomaApi.getVacancies(
                    token = dto.token,
                    area = dto.area,
                    industry = dto.industry,
                    text = dto.text,
                    salary = dto.salary,
                    page = dto.page,
                    onlyWithSalary = dto.onlyWithSalary
                ) }
            }

            is AreaRequest -> {
                handle { diplomaApi.getAreas(token = dto.token) }
            }

            is IndustryRequest -> {
                handle { diplomaApi.getIndustries(token = dto.token) }
            }

            is VacancyDetailRequest -> {
                handle { diplomaApi.getVacancy(token = dto.token, id = dto.id) }
            }

            else -> Response<Any>().apply { resultCode = CODE_400 }
        } as Response<T>
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    /** Универсальный обработчик всех запросов на сервер */
     private suspend fun <T> handle(block: suspend () -> retrofit2.Response<T>): Response<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = block()
                // Помещаем данные от сервера в конструктор (из-за того, что используем <out T> данные неизменяемы)
                val result = Response<T>(resultProp = response.body())
                result.resultCode = response.code()

                result
            } catch (e: Throwable) {
                Response<T>().apply { resultCode = CODE_500 }
            }
        }
    }
}
