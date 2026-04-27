package ru.practicum.android.diploma.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity

@Dao
interface FavoritesVacancyDao {

    @Query("SELECT * FROM vacancy_table")
    fun getAllFavorites(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancy_table WHERE id = :vacancyId")
    suspend fun getFavoriteByID(vacancyId: String): VacancyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vacancy: VacancyEntity)

    @Query("DELETE FROM vacancy_table WHERE id = :vacancyId")
    suspend fun delete(vacancyId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM vacancy_table WHERE id = :vacancyId)")
    suspend fun isFavorite(vacancyId: String): Boolean
}
