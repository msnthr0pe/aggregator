package ru.practicum.android.diploma.favorites.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.core.domain.favorites.interactor.FavoritesInteractor
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

sealed class FavoritesState {
    object Empty : FavoritesState()
    object Error : FavoritesState()
    data class Content(val vacancies: List<VacancyDetails>) : FavoritesState()
}

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.value = FavoritesState.Empty
            try {
                favoritesInteractor.getAllFavorites().collect { vacancies ->
                    processResult(vacancies)
                }
            } catch (_: Exception) {
                _state.value = FavoritesState.Error
            }
        }
    }

    private fun processResult(vacancies: List<VacancyDetails>) {
        if (vacancies.isEmpty()) {
            _state.value = FavoritesState.Empty
        } else {
            _state.value = FavoritesState.Content(vacancies)
        }
    }
}
