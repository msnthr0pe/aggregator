package ru.practicum.android.diploma.vacancysearch.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.core.util.debounce
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor
import ru.practicum.android.diploma.vacancysearch.ui.state.VacancySearchState

class VacancySearchViewModel(
    private val vacancySearchInteractor: VacancySearchInteractor
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20 // Кол-во элементов на странице
    }
    private val pageLiveData = MutableLiveData<VacancySearchState>(VacancySearchState.Nothing)
    fun observePage(): LiveData<VacancySearchState> = pageLiveData

    private val _searchQuery = MutableStateFlow("") // Для динамического обновления списка
    var latestSearchQuery: String = ""

    // Тут будут данные по фильтрам (SearchFilters)


    @OptIn(ExperimentalCoroutinesApi::class)
    val items = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            Pager(PagingConfig(pageSize = 20)) {
                VacancyPagingSource(vacancySearchInteractor, filters = mapOf(
                    "text" to query
                ))
            }.flow.cachedIn(viewModelScope)
        }
    }

    private val onSearchDebounce = debounce<String>(
        waitMs = 2000L,
        scope = viewModelScope,
        destinationFunction = { query ->
            if(!query.isEmpty() && query != latestSearchQuery) {
                searchVacancy(query) // При поиске всегда открывается 1 страница
            }

            latestSearchQuery = query
        }
    )

    fun onSearchQueryChanged(query: String) {
        onSearchDebounce(query)
    }

    /** Тестовый метод для поиска вакансий */
    fun searchVacancy(query: String) {
        _searchQuery.value = query

//        viewModelScope.launch {
//
//            Log.i("QUERY", query)
////            pageLiveData.postValue(VacancySearchState.Loading)
////
////            vacancySearchInteractor.vacancySearch(
////                token = "Bearer ${BuildConfig.API_ACCESS_TOKEN}",
////                filters = mapOf(
////                    "text" to query,
////                    "page" to page.toString()
////                ),
////            ).collect { result ->
////                result.onSuccess { info ->
////                    if (info.items.isEmpty()) {
////                        pageLiveData.postValue(VacancySearchState.Empty)
////                    } else {
////                        pageLiveData.postValue(VacancySearchState.Success(info.items))
////                    }
////                }
////
////                result.onFailure { exception ->
////                    pageLiveData.postValue(VacancySearchState.Error(exception.message.toString()))
////                }
////            }
//        }
    }
}
