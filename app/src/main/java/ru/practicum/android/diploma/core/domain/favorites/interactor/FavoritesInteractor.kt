package ru.practicum.android.diploma.core.domain.favorites.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity

interface FavoritesInteractor {

    fun getAllFavorites(): Flow<List<VacancyEntity>>

    suspend fun addToFavorites(vacancy: VacancyEntity)

    suspend fun removeFromFavorites(vacancyId: String)

    suspend fun getFavoriteById(vacancyId: String): VacancyEntity?

    suspend fun isFavorite(vacancyId: String): Boolean

}
