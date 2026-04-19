package ru.practicum.android.diploma.core

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.practicum.android.diploma.core.di.dataModule
import ru.practicum.android.diploma.core.di.interactorModule
import ru.practicum.android.diploma.core.di.repositoryModule
import ru.practicum.android.diploma.core.di.viewModelModule

class AggregatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AggregatorApp)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}
