package ru.practicum.android.diploma.settingsfilter.ui.util

import android.os.Bundle
import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_AREA
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_ID
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_NAME
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_ONLY_WITH_SALARY
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_PENDING_INDUSTRY_ID
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_PENDING_INDUSTRY_NAME
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_SALARY

fun getFilters(args: Bundle): SearchFilters {
    return SearchFilters(
        areaCountry = args.getInt(KEY_AREA)
            .takeIf { it != 0 }
            ?.let { SearchFilters.AreaCountry(it, "") },
        industry = args.getInt(KEY_INDUSTRY_ID)
            .takeIf { it != 0 }
            ?.let { SearchFilters.Industry(it, args.getString(KEY_INDUSTRY_NAME) ?: "") },
        salary = args.getInt(KEY_SALARY).takeIf { it != 0 },
        showSalary = args.getBoolean(KEY_ONLY_WITH_SALARY, false)
    )
}

fun putFilters(filters: SearchFilters, pendingIndustry: Pair<Int, String>? = null): Bundle {
    return Bundle().apply {
        filters.areaCountry?.id?.let { putInt(KEY_AREA, it) }
        filters.industry?.id?.let { putInt(KEY_INDUSTRY_ID, it) }
        filters.industry?.name?.let { putString(KEY_INDUSTRY_NAME, it) }
        pendingIndustry?.first?.let { putInt(KEY_PENDING_INDUSTRY_ID, it) }
        pendingIndustry?.second?.let { putString(KEY_PENDING_INDUSTRY_NAME, it) }
        filters.salary?.let { putInt(KEY_SALARY, it) }
        filters.showSalary?.let { putBoolean(KEY_ONLY_WITH_SALARY, it) }
    }
}
