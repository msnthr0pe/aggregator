package ru.practicum.android.diploma.core.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.core.data.network.DiplomaApi

const val BASE_URL = "https://android-diploma.education-services.ru"

val dataModule = module {

    single<DiplomaApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiplomaApi::class.java)
    }
}
