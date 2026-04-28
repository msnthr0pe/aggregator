package ru.practicum.android.diploma.core.util

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.ImageView
import co.touchlab.stately.concurrency.AtomicBoolean
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.data.dto.area.AreaDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

const val DEFAULT_DEBOUNCE_DELAY = 300L
private const val GROUP_SIZE = 3

fun Context.hasNetwork(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    if (network == null || capabilities == null) {
        return false
    }
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}

fun <T> debounce(
    waitMs: Long = DEFAULT_DEBOUNCE_DELAY,
    scope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun clickDebounce(
    waitMs: Long = DEFAULT_DEBOUNCE_DELAY,
    scope: CoroutineScope,
    isClickAllowed: AtomicBoolean
): Boolean {
    val current = isClickAllowed
    if (isClickAllowed.value) {
        isClickAllowed.value = false
        scope.launch {
            delay(waitMs)
            isClickAllowed.value = true
        }
    }
    return current.value
}

fun VacancyDetails.formatSalary(resources: Resources): String {
    val salary = this.salary
    val fromText = resources.getString(R.string.salary_from)
    val toText = resources.getString(R.string.salary_to)
    val currency = salary?.currency.orEmpty()

    fun Int.formatWithSpaces(): String {
        return this.toString()
            .reversed()
            .chunked(GROUP_SIZE)
            .joinToString(" ")
            .reversed()
    }

    return when {
        salary == null -> resources.getString(R.string.salary_not_specified)

        salary.from != null && salary.to != null -> {
            "$fromText ${salary.from.formatWithSpaces()} $toText ${salary.to.formatWithSpaces()} $currency"
        }

        salary.from != null -> {
            "$fromText ${salary.from.formatWithSpaces()} $currency"
        }

        salary.to != null -> {
            "$toText ${salary.to.formatWithSpaces()} $currency"
        }

        else -> resources.getString(R.string.salary_not_specified)
    }
}

fun loadPicInto(context: Context, url: String, image: ImageView) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .into(image)
}

/**
 *  Функция чтобы легче получать токен и не писать нигде вручную Bearer
 */
fun getToken(): String = "Bearer ${BuildConfig.API_ACCESS_TOKEN}"

/**
 * Функция чтобы легче было делать логи для отладки
 */
fun tag(contents: Any?, tag: String = "customtag") {
    Log.d(tag, contents.toString())
}

fun VacancyDetailDto.SalaryDto.toDomain(): VacancyDetails.Salary =
    VacancyDetails.Salary(
        from = from,
        to = to,
        currency = currency,
    )

fun VacancyDetailDto.AddressDto.toDomain(): VacancyDetails.Address =
    VacancyDetails.Address(
        id = id,
        city = city,
        street = street,
        building = building,
        raw = raw,
    )

fun VacancyDetailDto.ExperienceDto.toDomain(): VacancyDetails.Experience =
    VacancyDetails.Experience(
        id = id,
        name = name,
    )

fun VacancyDetailDto.ScheduleDto.toDomain(): VacancyDetails.Schedule =
    VacancyDetails.Schedule(
        id = id,
        name = name,
    )

fun VacancyDetailDto.EmploymentDto.toDomain(): VacancyDetails.Employment =
    VacancyDetails.Employment(
        id = id,
        name = name,
    )

fun VacancyDetailDto.ContactsDto.toDomain(): VacancyDetails.Contacts =
    VacancyDetails.Contacts(
        id = id,
        name = name,
        email = email,
        phones = phones.map {
            VacancyDetails.Phone(
                comment = it.comment,
                formatted = it.formatted,
            )
        },
    )

fun VacancyDetailDto.EmployerDto.toDomain(): VacancyDetails.Employer =
    VacancyDetails.Employer(
        id = id,
        name = name,
        logo = logo,
    )

fun AreaDto.toDomain(): VacancyDetails.Area =
    VacancyDetails.Area(
        id = id,
        name = name,
        parentId = parentId,
        areas = areas.map { toDomain() },
    )

fun IndustryDto.toDomain(): VacancyDetails.Industry =
    VacancyDetails.Industry(
        id = id,
        name = name,
    )
