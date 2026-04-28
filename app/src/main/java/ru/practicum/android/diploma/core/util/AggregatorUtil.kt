package ru.practicum.android.diploma.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.ImageView
import co.touchlab.stately.concurrency.AtomicBoolean
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Headers
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.data.dto.area.AreaDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.LeadingMarginSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

const val DEFAULT_DEBOUNCE_DELAY = 300L

fun TextView.setPrettyHtml(html: String) {
    // 1) Базовый парсинг
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val ssb = SpannableStringBuilder(spanned)

    // 2) Соберём жирные диапазоны (обычно заголовки h2/h3 после fromHtml)
    data class BoldRange(val text: String)

    val boldRanges = ssb.getSpans<StyleSpan>()
        .filter { it.style == Typeface.BOLD }
        .map { span ->
            val s = ssb.getSpanStart(span).coerceAtLeast(0)
            val e = ssb.getSpanEnd(span).coerceAtLeast(0)
            BoldRange(ssb.substring(s, e).trim())
        }
        .filter { it.text.isNotBlank() }
        .distinctBy { it.text }

    // 3) Готовим rebuilt заранее
    val rebuilt = SpannableStringBuilder()

    fun applyHeaderStyleIfNeeded(
        target: SpannableStringBuilder,
        start: Int,
        end: Int,
        lineText: String
    ) {
        val trimmed = lineText.trim()
        val isHeader = boldRanges.any { it.text == trimmed }
        if (!isHeader) return

        // Эвристика "большевизны": длиннее => h2 (22sp), короче => h3 (16sp)
        val sizeSp = if (trimmed.length >= 18) 22 else 16

        target.setSpan(
            StyleSpan(Typeface.BOLD),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        target.setSpan(
            AbsoluteSizeSpan(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    sizeSp.toFloat(),
                    resources.displayMetrics
                ).toInt()
            ),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    // 4) Буллеты для списков (ваш подход)
    val text = ssb.toString()
    val lines = text.split('\n')

    var inListBlock = false

    fun appendLineWithBullet(line: String) {
        val start = rebuilt.length
        rebuilt.append(line.trim())
        val end = rebuilt.length

        val indentPx = (this.textSize * 1.6f).toInt()
        rebuilt.setSpan(
            LeadingMarginSpan.Standard(indentPx),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    lines.forEach { raw ->
        val line = raw.trim()
        if (line.isBlank()) return@forEach

        if (line.equals("Обязанности", ignoreCase = true) ||
            line.equals("Требования", ignoreCase = true) ||
            line.equals("Условия", ignoreCase = true)
        ) {
            inListBlock = true

            val start = rebuilt.length
            rebuilt.append(line)
            val end = rebuilt.length
            applyHeaderStyleIfNeeded(rebuilt, start, end, line)

            rebuilt.append("\n")
            return@forEach
        }

        if (inListBlock) {
            rebuilt.append("• ")
            appendLineWithBullet(line)
            rebuilt.append("\n")
        } else {
            val start = rebuilt.length
            rebuilt.append(line)
            val end = rebuilt.length
            applyHeaderStyleIfNeeded(rebuilt, start, end, line)

            rebuilt.append("\n\n")
        }
    }

    // Если вдруг rebuilt пустой — покажем базовый результат
    this.text = if (rebuilt.isNotBlank()) rebuilt else ssb
}

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

fun loadPicInto(context: Context, url: String, image: ImageView) {
    Glide.with(context)
        .load(url)
        .fitCenter()
        .into(image)
}

fun loadSvgInto(url: String, image: ImageView) {
    val imageLoader = ImageLoader.Builder(image.context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    image.load(url, imageLoader) {
        placeholder(R.drawable.vacancy_placeholder)
        headers(
            Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Android)")
                .add("Accept", "image/svg+xml,image/*,*/*;q=0.8")
                .build()
        )
    }
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
