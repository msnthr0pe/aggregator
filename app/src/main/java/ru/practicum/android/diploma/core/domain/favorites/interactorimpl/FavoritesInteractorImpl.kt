package ru.practicum.android.diploma.core.domain.favorites.interactorimpl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.core.domain.favorites.interactor.FavoritesInteractor
import ru.practicum.android.diploma.core.domain.favorites.repository.FavoritesRepository

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override fun getAllFavorites(): Flow<List<VacancyEntity>> {
        return repository.getAllFavorites()
    }

    override suspend fun addToFavorites(vacancy: VacancyEntity) {
        repository.addToFavorites(vacancy)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        repository.removeFromFavorites(vacancyId)
    }

    override suspend fun getFavoriteById(vacancyId: String): VacancyEntity? {
        return repository.getFavoriteById(vacancyId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return repository.isFavorite(vacancyId)
    }

}
