package ru.practicum.android.diploma.core.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.chooseindustry.presentation.ChooseIndustryViewModel
import ru.practicum.android.diploma.favorites.presentation.FavoritesViewModel
import ru.practicum.android.diploma.vacancy.presentation.VacancyViewModel
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchViewModel

val viewModelModule = module {

    viewModel {
        VacancySearchViewModel(get())
    }

    viewModel {
        VacancyViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        ChooseIndustryViewModel(get())
    }
}
