package ru.practicum.android.diploma.core.data.favorites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.core.data.db.AppDatabase
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.core.domain.favorites.repository.FavoritesRepository
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoritesRepository {

    private val favoritesDao = appDatabase.favoritesDao()

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return favoritesDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                entity.fullDetails
            }
        }
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        val entity = VacancyEntity(
            id = vacancy.id,
            name = vacancy.name,
            company = vacancy.employer.name,
            city = vacancy.address?.city,
            logo = vacancy.employer.logo,
            salaryFrom = vacancy.salary?.from,
            salaryTo = vacancy.salary?.to,
            salaryCurrency = vacancy.salary?.currency,
            fullDetails = vacancy
        )
        favoritesDao.insert(entity)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        favoritesDao.delete(vacancyId)
    }

    override suspend fun getFavoriteById(vacancyId: String): VacancyDetails? {
        val entity = favoritesDao.getFavoriteByID(vacancyId)
        return entity?.fullDetails
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return favoritesDao.isFavorite(vacancyId)
    }

}
