package ru.practicum.android.diploma.core.di

import org.koin.dsl.module
import ru.practicum.android.diploma.core.data.favorites.FavoritesRepositoryImpl
import ru.practicum.android.diploma.core.domain.favorites.repository.FavoritesRepository
import ru.practicum.android.diploma.vacancy.data.impl.VacancyDetailsRepositoryImpl
import ru.practicum.android.diploma.vacancy.domain.api.VacancyDetailsRepository
import ru.practicum.android.diploma.vacancysearch.data.impl.VacancySearchRepositoryImpl
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

val repositoryModule = module {

    factory<VacancySearchRepository> {
        VacancySearchRepositoryImpl(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get())
    }

    single<VacancyDetailsRepository> {
        VacancyDetailsRepositoryImpl(get())
    }

}
