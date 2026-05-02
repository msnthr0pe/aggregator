package ru.practicum.android.diploma.core.di

import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.core.data.db.AppDatabase
import ru.practicum.android.diploma.core.data.db.converter.VacancyTypeConverters
import ru.practicum.android.diploma.core.data.network.DiplomaApi
import ru.practicum.android.diploma.core.data.network.NetworkClient
import ru.practicum.android.diploma.core.data.network.RetrofitNetworkClient

const val BASE_URL = "https://android-diploma.education-services.ru"

val dataModule = module {

    single<Gson> {
        GsonBuilder()
            .serializeNulls()
            .create()
    }

    single<DiplomaApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiplomaApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addTypeConverter(VacancyTypeConverters(get()))
            .build()
    }
}
