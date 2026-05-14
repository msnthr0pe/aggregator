package ru.practicum.android.diploma.settingsfilter.domain.api

import ru.practicum.android.diploma.core.domain.models.SearchFilters

interface FilterSettingsInteractor {
    fun saveFilters(filters: SearchFilters?)
    fun getFilters(): SearchFilters?
}
