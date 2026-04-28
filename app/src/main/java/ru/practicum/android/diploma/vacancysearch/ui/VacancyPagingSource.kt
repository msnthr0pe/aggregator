package ru.practicum.android.diploma.vacancysearch.ui

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.first
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor
import java.io.IOException

class VacancyPagingSource(
    val vacancySearchInteractor: VacancySearchInteractor,
    val filters: Map<String, String>
) : PagingSource<Int, VacancyCard>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VacancyCard> {
        return try {

            val page = params.key ?: 1 // API страницы начинается с 1
            val result = vacancySearchInteractor.vacancySearch(
                token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}",
                filters = filters
            ).first()

            return if (result.isSuccess) {
                val response = result.getOrNull()
                val vacancies = response?.items ?: emptyList()

                Log.i("TEEEEEEST_123", vacancies.size.toString())

                LoadResult.Page(
                    data = vacancies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (vacancies.isEmpty()) null else page + 1
                )
            } else {
                val error = result.exceptionOrNull() ?: Exception("Unknown error")
                LoadResult.Error(error)
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    /** С какого элемента начинается подгрузка */
    override fun getRefreshKey(state: PagingState<Int, VacancyCard>): Int? {
        return state.anchorPosition
    }
}
