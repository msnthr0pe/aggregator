package ru.practicum.android.diploma.vacancy.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.core.domain.favorites.interactor.FavoritesInteractor
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.vacancy.model.VacancyPageState
import ru.practicum.android.diploma.vacancy.model.VacancyState

class VacancyViewModel(
    private val interactor: VacancyDetailsInteractor,
    private val interactorFavorites: FavoritesInteractor
) : ViewModel() {
    private var vacancyState = VacancyState(vacancyDetails = null, isFavorite = false)
    private val _vacancyStateLiveData = MutableLiveData<VacancyPageState>(VacancyPageState.Loading)
    fun observeVacancyDetails(): LiveData<VacancyPageState> = _vacancyStateLiveData

    fun getVacancyDetails(): VacancyDetails? {
        return vacancyState.vacancyDetails
    }

    /** Проверка на избранное + запрос на сервер за данными в ОДНОМ потоке */
    fun init(vacancyId: String) {
        viewModelScope.launch {
            val isFavorite = interactorFavorites.isFavorite(vacancyId)

            interactor
                .getVacancyInfo(vacancyId)
                .collect { result ->
                    result.onSuccess {
                        vacancyState = vacancyState.copy(vacancyDetails = it, isFavorite = isFavorite)
                        _vacancyStateLiveData.postValue(VacancyPageState.Success(vacancyState))
                    }

                    result.onFailure {
                        _vacancyStateLiveData.postValue(VacancyPageState.Error(it.message.toString()))
                    }
                }
        }
    }

    /** Добавление в избранное или удаление */
    fun toggleRequestFavorite() {
        viewModelScope.launch {
            if (vacancyState.isFavorite) {
                interactorFavorites.removeFromFavorites(vacancyState.vacancyDetails?.id ?: "")
            } else {
                interactorFavorites.addToFavorites(vacancyState.vacancyDetails!!)
            }

            vacancyState = vacancyState.copy(isFavorite = !vacancyState.isFavorite)
            _vacancyStateLiveData.postValue(VacancyPageState.Success(vacancyState))
        }
    }
}
