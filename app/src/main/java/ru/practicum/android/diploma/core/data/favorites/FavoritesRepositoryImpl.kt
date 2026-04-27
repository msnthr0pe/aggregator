package ru.practicum.android.diploma.core.data.favorites

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.db.dao.FavoritesVacancyDao
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.core.domain.favorites.repository.FavoritesRepository

class FavoritesRepositoryImpl(
    private val favoritesDao: FavoritesVacancyDao
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<VacancyEntity>> {
        return favoritesDao.getAllFavorites()
    }

    override suspend fun addToFavorites(vacancy: VacancyEntity) {
        favoritesDao.insert(vacancy)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        favoritesDao.delete(vacancyId)
    }

    override suspend fun getFavoriteById(vacancyId: String): VacancyEntity? {
        return favoritesDao.getFavoriteByID(vacancyId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return favoritesDao.isFavorite(vacancyId)
    }

}
