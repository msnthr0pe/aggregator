package ru.practicum.android.diploma.core.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchViewModels

val viewModelModule = module {

    viewModel {
        VacancySearchViewModels(get())
    }
}
