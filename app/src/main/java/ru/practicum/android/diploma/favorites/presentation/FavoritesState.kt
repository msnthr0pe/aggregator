package ru.practicum.android.diploma.favorites.presentation

import ru.practicum.android.diploma.core.domain.models.VacancyDetails

sealed class FavoritesState {
    object Empty : FavoritesState()
    object Error : FavoritesState()
    data class Content(val vacancies: List<VacancyDetails>) : FavoritesState()
}
