package ru.practicum.android.diploma.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.core.data.db.entity.VacancyEntity

@Database(version = 1, exportSchema = false, entities = [VacancyEntity::class])
abstract class AppDatabase : RoomDatabase()
