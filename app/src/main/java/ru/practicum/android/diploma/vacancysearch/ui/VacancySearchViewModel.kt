package ru.practicum.android.diploma.vacancysearch.ui

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
    private var foundVacanciesAmount = -1

    // Тут будут данные по фильтрам (SearchFilters)

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            val pagingSourceFactory =
                VacancyPagingSource(
                    vacancySearchInteractor,
                    filters = mapOf("text" to query)
                )
            foundVacanciesAmount = pagingSourceFactory.getFoundVacanciesAmount()
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                pagingSourceFactory
            }.flow.cachedIn(viewModelScope)
        }
    }

    val onSearchDebounce = debounce<String>(
        waitMs = 2000L,
        scope = viewModelScope,
        destinationFunction = { query ->
            if (!query.isEmpty() && query != latestSearchQuery) {
                _searchQuery.value = query
            }

            latestSearchQuery = query
        }
    )

    /** Обновление данных для страницы */
    fun updatePageLiveData(data: VacancySearchState) {
        val state = if (data is VacancySearchState.Success) {
            data.copy(foundItems = foundVacanciesAmount)
        } else {
            data
        }
        pageLiveData.postValue(state)
    }
}
