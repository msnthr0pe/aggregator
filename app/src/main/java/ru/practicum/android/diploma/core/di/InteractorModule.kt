package ru.practicum.android.diploma.core.di

import org.koin.dsl.module
import ru.practicum.android.diploma.vacancysearch.domain.api.VacancySearchInteractor
import ru.practicum.android.diploma.vacancysearch.domain.impl.VacancySearchInteractorImpl

val interactorModule = module {

    factory<VacancySearchInteractor> {
        VacancySearchInteractorImpl(get())
    }
}
