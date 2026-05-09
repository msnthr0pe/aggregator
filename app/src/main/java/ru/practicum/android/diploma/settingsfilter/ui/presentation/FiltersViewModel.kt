package ru.practicum.android.diploma.settingsfilter.ui.presentation

import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.core.domain.models.SearchFilters

class FiltersViewModel : ViewModel() {
    private var filters: SearchFilters? = null

    private var areaCountryId: Int? = null
    private var industryId: Int? = null
    private var industryName: String? = null
    private var salary: Int? = null
    private var showSalary: Boolean? = null

    fun setFilters(searchFilters: SearchFilters) {
        filters = searchFilters
        industryId = searchFilters.industry?.id
        industryName = searchFilters.industry?.name
    }

    fun updateFilters(
        areaCountryId: Int? = null,
        industryId: Int? = null,
        industryName: String? = null,
        salary: Int? = null,
        showSalary: Boolean? = null,
        clearIndustrySelection: Boolean = false
    ) {
        areaCountryId?.let { this.areaCountryId = it }
        industryId?.let { this.industryId = it }
        industryName?.let { this.industryName = it }
        if (clearIndustrySelection) {
            this.industryId = null
            this.industryName = null
        }
        salary?.let { this.salary = it }
        showSalary?.let { this.showSalary = it }
        updateFilters()
    }

    private fun updateFilters() {
        val areaCountry = areaCountryId?.let {
            SearchFilters.AreaCountry(it)
        }
        val industry = industryId?.let {
            SearchFilters.Industry(
                it,
                industryName.orEmpty()
            )
        }
        val searchFilters = SearchFilters(
            areaCountry = areaCountry,
            areaRegion = null,
            industry = industry,
            salary = salary,
            showSalary = showSalary,
        )
        filters = searchFilters
    }

    fun resetFilters() {
        filters = null
    }

    fun getCurrentFilters(): SearchFilters? = filters
}
