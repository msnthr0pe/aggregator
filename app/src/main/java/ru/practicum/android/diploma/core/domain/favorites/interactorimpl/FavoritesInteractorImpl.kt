package ru.practicum.android.diploma.core.domain.favorites.interactorimpl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.domain.favorites.interactor.FavoritesInteractor
import ru.practicum.android.diploma.core.domain.favorites.repository.FavoritesRepository
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return repository.getAllFavorites()
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        repository.addToFavorites(vacancy)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        repository.removeFromFavorites(vacancyId)
    }

    override suspend fun getFavoriteById(vacancyId: String): VacancyDetails? {
        return repository.getFavoriteById(vacancyId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return repository.isFavorite(vacancyId)
    }

}
