package ru.practicum.android.diploma.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacancy_table")
data class VacancyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val company: String?,
    val city: String?,
    val logo: String?
)
