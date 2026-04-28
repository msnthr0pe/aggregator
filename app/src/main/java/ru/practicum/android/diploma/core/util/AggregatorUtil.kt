package ru.practicum.android.diploma.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
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
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.data.dto.area.AreaDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

const val DEFAULT_DEBOUNCE_DELAY = 300L
private const val GROUP_SIZE = 3

fun TextView.setPrettyHtmlByTags(html: String) {
    val doc = Jsoup.parseBodyFragment(html)
    val out = SpannableStringBuilder()

    fun appendText(text: String) {
        val t = text.replace('\u00A0', ' ').trim()
        if (t.isNotBlank()) out.append(t)
    }

    fun appendNewLine(count: Int = 1) {
        repeat(count) {
            if (out.isNotEmpty() && out.last() != '\n') {
                out.append('\n')
            } else if (out.isNotEmpty()) {
                out.append('\n')
            }
        }
    }

    fun applyHeaderSpan(start: Int, end: Int, level: Int) {
        val sizePx = when (level) {
            1 -> resources.getDimensionPixelSize(R.dimen.text_size_xl)
            2 -> resources.getDimensionPixelSize(R.dimen.text_size_lg)
            3 -> resources.getDimensionPixelSize(R.dimen.text_size_md)
            else -> resources.getDimensionPixelSize(R.dimen.text_size_sm)
        }

        out.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        out.setSpan(AbsoluteSizeSpan(sizePx), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun walk(node: Node) {
        when (node) {
            is TextNode -> {
                appendText(node.text())
            }

            is Element -> {
                when (node.tagName().lowercase()) {
                    "h1", "h2", "h3", "h4", "h5", "h6" -> {
                        val level = node.tagName().substring(1).toIntOrNull() ?: 3
                        val start = out.length
                        appendText(node.text())
                        val end = out.length
                        applyHeaderSpan(start, end, level)
                    }

                    "p" -> {
                        if (out.isNotEmpty()) appendNewLine(1)
                        node.childNodes().forEach { walk(it) }
                        appendNewLine(1)
                    }

                    "ul" -> {
                        if (out.isNotEmpty()) appendNewLine(1)
                        node.children().forEach { li ->
                            if (li.tagName().equals("li", ignoreCase = true)) {
                                out.append("• ")
                                appendText(li.text())
                                appendNewLine(1)
                            }
                        }
                    }

                    "br" -> appendNewLine(1)

                    else -> {
                        appendNewLine(1)
                        node.childNodes().forEach { walk(it) }
                    }
                }
            }
        }
    }

    doc.body().childNodes().forEach { walk(it) }

    text = out
}

fun Context.openDialer(phone: String) {
    val uri = "tel:${Uri.encode(phone)}".toUri()
    val intent = Intent(Intent.ACTION_DIAL, uri)

    if (this !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    startActivity(Intent.createChooser(intent, getString(R.string.call_via)))
}

fun Context.sendEmail(email: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = "mailto:".toUri()
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    startActivity(intent)
}

fun Context.shareVacancy(url: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, url)
    intent.type = "text/plain"
    startActivity(Intent.createChooser(intent, null))
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
    destinationFunction: (T) -> Unit,
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
    isClickAllowed: AtomicBoolean,
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
