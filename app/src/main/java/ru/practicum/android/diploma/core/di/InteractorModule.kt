package ru.practicum.android.diploma.core.di

import org.koin.dsl.module
import ru.practicum.android.diploma.core.domain.favorites.interactor.FavoritesInteractor
import ru.practicum.android.diploma.core.domain.favorites.interactorImpl.FavoritesInteractorImpl
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.vacancy.domain.impl.VacancyDetailsInteractorImpl
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor
import ru.practicum.android.diploma.vacancysearch.domain.impl.VacancySearchInteractorImpl

val interactorModule = module {

    factory<VacancySearchInteractor> {
        VacancySearchInteractorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    single<VacancyDetailsInteractor> {
        VacancyDetailsInteractorImpl(get())
    }
}
