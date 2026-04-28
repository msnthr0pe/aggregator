package ru.practicum.android.diploma.core.data.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

@ProvidedTypeConverter
class VacancyTypeConverters(
    private val gson: Gson
) {

    @TypeConverter
    fun fromVacancyDetails(vacancyDetails: VacancyDetails): String {
        return gson.toJson(vacancyDetails)
    }

    @TypeConverter
    fun toVacancyDetails(json: String): VacancyDetails {
        return gson.fromJson(json, VacancyDetails::class.java)
    }
}
