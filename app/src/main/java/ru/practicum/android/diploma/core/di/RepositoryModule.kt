package ru.practicum.android.diploma.core.di

import org.koin.dsl.module
import ru.practicum.android.diploma.vacancysearch.data.impl.VacancySearchRepositoryImpl
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchRepository

val repositoryModule = module {

    factory<VacancySearchRepository> {
        VacancySearchRepositoryImpl(get())
    }
}
