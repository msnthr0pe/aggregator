package ru.practicum.android.diploma.core.domain.favorites.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

interface FavoritesRepository {

    fun getAllFavorites(): Flow<List<VacancyDetails>>

    suspend fun addToFavorites(vacancy: VacancyDetails)

    suspend fun removeFromFavorites(vacancyId: String)

    suspend fun getFavoriteById(vacancyId: String): VacancyDetails?

    suspend fun isFavorite(vacancyId: String): Boolean
}
