package ru.practicum.android.diploma.vacancysearch.util

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.core.ui.state.PlaceholderType
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_AREA
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_ID
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_NAME
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_ONLY_WITH_SALARY
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_SALARY

fun getFilters(bundle: Bundle): SearchFilters {
    val onlyWithSalary = bundle.getBoolean(KEY_ONLY_WITH_SALARY, false)

    return SearchFilters(
        areaCountry = bundle.getInt(KEY_AREA, 0)
            .takeIf { it != 0 }
            ?.let { SearchFilters.AreaCountry(it, "") },
        industry = bundle.getInt(KEY_INDUSTRY_ID, 0)
            .takeIf { it != 0 }
            ?.let { SearchFilters.Industry(it, bundle.getString(KEY_INDUSTRY_NAME, "")) },
        salary = bundle.getInt(KEY_SALARY, 0).takeIf { it != 0 },
        showSalary = onlyWithSalary
    )
}

/** Изначальное отображение страницы */
fun FragmentVacancySearchBinding.showNothing(context: Context) {
    recyclerList.visibility = View.GONE
    buttonCount.visibility = View.GONE
    progressBar.visibility = View.GONE
    placeholder.placeholderInfo.visibility = View.VISIBLE
    buttonCount.visibility = View.GONE

    initPlaceholder(context, PlaceholderType.NOTHING, "")
}

/** Отображение пустой страницы */
fun FragmentVacancySearchBinding.showEmpty(context: Context) {
    recyclerList.visibility = View.GONE
    buttonCount.visibility = View.GONE
    progressBar.visibility = View.GONE
    placeholder.placeholderInfo.visibility = View.VISIBLE
    buttonCount.visibility = View.GONE

    initPlaceholder(context, PlaceholderType.EMPTY, context.getString(R.string.favorites_error_load))
}

/** Отображение загрузки */
fun FragmentVacancySearchBinding.showLoading() {
    recyclerList.visibility = View.GONE
    buttonCount.visibility = View.GONE
    progressBar.visibility = View.VISIBLE
    placeholder.placeholderInfo.visibility = View.GONE
    buttonCount.visibility = View.GONE
}

/** Отображение ошибки */
fun FragmentVacancySearchBinding.showError(context: Context, serverCode: String) {
    recyclerList.visibility = View.GONE
    buttonCount.visibility = View.GONE
    progressBar.visibility = View.GONE
    placeholder.placeholderInfo.visibility = View.VISIBLE
    buttonCount.visibility = View.GONE

    val message = when (serverCode) {
        "-1" -> context.getString(R.string.no_internet)
        else -> context.getString(R.string.error)
    }

    initPlaceholder(context, PlaceholderType.ERROR, message)
}

/** Отображение списка вакансий */
fun FragmentVacancySearchBinding.showSuccess(context: Context, foundVacanciesAmount: Int) {
    recyclerList.visibility = View.VISIBLE
    buttonCount.visibility = View.GONE
    progressBar.visibility = View.GONE
    placeholder.placeholderInfo.visibility = View.GONE
    if (foundVacanciesAmount != -1) {
        buttonCount.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.vacancies_found_count, foundVacanciesAmount)
        }
    }
}

/** Отрисовка placeholder */
private fun FragmentVacancySearchBinding.initPlaceholder(context: Context, type: PlaceholderType, message: String) {
    val imgElement = placeholder.placeholderInfoImg
    val textElement = placeholder.placeholderInfoText
    val imgUrl = when (type) {
        PlaceholderType.NOTHING -> R.drawable.placeholder
        PlaceholderType.ERROR -> R.drawable.placeholder_2
        PlaceholderType.EMPTY -> R.drawable.favorites_error_load
    }

    Glide.with(context)
        .load(imgUrl)
        .into(imgElement)

    textElement.text = message
}
