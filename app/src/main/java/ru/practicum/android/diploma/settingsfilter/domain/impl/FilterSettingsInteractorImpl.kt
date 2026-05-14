package ru.practicum.android.diploma.settingsfilter.domain.impl

import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.settingsfilter.domain.api.FilterSettingsInteractor
import ru.practicum.android.diploma.settingsfilter.domain.api.FilterSettingsRepository

class FilterSettingsInteractorImpl(
    private val repository: FilterSettingsRepository
) : FilterSettingsInteractor {

    override fun saveFilters(filters: SearchFilters?) {
        repository.saveFilters(filters)
    }

    override fun getFilters(): SearchFilters? {
        return repository.getFilters()
    }
}
