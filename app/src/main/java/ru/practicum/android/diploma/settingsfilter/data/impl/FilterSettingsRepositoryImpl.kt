package ru.practicum.android.diploma.settingsfilter.data.impl

import android.content.SharedPreferences
import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.settingsfilter.domain.api.FilterSettingsRepository

class FilterSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : FilterSettingsRepository {

    override fun saveFilters(filters: SearchFilters?) {
        sharedPreferences.edit().apply {
            if (filters == null) {
                clearAllFilters()
            } else {
                writeFilters(filters)
            }
            apply()
        }
    }

    override fun getFilters(): SearchFilters? {
        val areaCountryId = sharedPreferences.getInt(KEY_AREA_COUNTRY_ID, 0).takeIf { it != 0 }
        val industryId = sharedPreferences.getInt(KEY_INDUSTRY_ID, 0).takeIf { it != 0 }
        val industryName = sharedPreferences.getString(KEY_INDUSTRY_NAME, null)
        val salary = sharedPreferences.getInt(KEY_SALARY, 0).takeIf { it != 0 }
        val showSalary = sharedPreferences.getBoolean(KEY_SHOW_SALARY, false)
        val noLocationFilter = areaCountryId == null && industryId == null
        val noSalaryFilter = salary == null && !showSalary
        if (noLocationFilter && noSalaryFilter) return null
        return SearchFilters(
            areaCountry = areaCountryId?.let { SearchFilters.AreaCountry(it) },
            industry = industryId?.let { SearchFilters.Industry(it, industryName.orEmpty()) },
            salary = salary,
            showSalary = showSalary
        )
    }

    private fun SharedPreferences.Editor.clearAllFilters() {
        remove(KEY_AREA_COUNTRY_ID)
        remove(KEY_INDUSTRY_ID)
        remove(KEY_INDUSTRY_NAME)
        remove(KEY_SALARY)
        remove(KEY_SHOW_SALARY)
    }

    private fun SharedPreferences.Editor.writeFilters(filters: SearchFilters) {
        writeAreaCountry(filters.areaCountry)
        writeIndustry(filters.industry)
        writeSalary(filters.salary)
        putBoolean(KEY_SHOW_SALARY, filters.showSalary == true)
    }

    private fun SharedPreferences.Editor.writeAreaCountry(areaCountry: SearchFilters.AreaCountry?) {
        if (areaCountry != null) {
            putInt(KEY_AREA_COUNTRY_ID, areaCountry.id)
        } else {
            remove(KEY_AREA_COUNTRY_ID)
        }
    }

    private fun SharedPreferences.Editor.writeIndustry(industry: SearchFilters.Industry?) {
        if (industry != null) {
            putInt(KEY_INDUSTRY_ID, industry.id)
            putString(KEY_INDUSTRY_NAME, industry.name)
        } else {
            remove(KEY_INDUSTRY_ID)
            remove(KEY_INDUSTRY_NAME)
        }
    }

    private fun SharedPreferences.Editor.writeSalary(salary: Int?) {
        if (salary != null) {
            putInt(KEY_SALARY, salary)
        } else {
            remove(KEY_SALARY)
        }
    }

    companion object {
        private const val KEY_AREA_COUNTRY_ID = "filter_area_country_id"
        private const val KEY_INDUSTRY_ID = "filter_industry_id"
        private const val KEY_INDUSTRY_NAME = "filter_industry_name"
        private const val KEY_SALARY = "filter_salary"
        private const val KEY_SHOW_SALARY = "filter_show_salary"
    }
}
