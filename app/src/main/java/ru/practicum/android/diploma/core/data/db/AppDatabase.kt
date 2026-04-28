package ru.practicum.android.diploma.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.practicum.android.diploma.core.data.db.converter.VacancyTypeConverters
import ru.practicum.android.diploma.core.data.db.dao.FavoritesVacancyDao
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity

@Database(version = 1, exportSchema = false, entities = [VacancyEntity::class])
@TypeConverters(VacancyTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesVacancyDao

}
