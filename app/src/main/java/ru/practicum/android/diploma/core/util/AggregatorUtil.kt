package ru.practicum.android.diploma.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.core.net.toUri
import co.touchlab.stately.concurrency.AtomicBoolean
import coil.decode.SvgDecoder
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.data.dto.area.AreaDto
import ru.practicum.android.diploma.core.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.core.data.dto.vacancydetail.VacancyDetailDto
import ru.practicum.android.diploma.core.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.core.domain.models.VacancyDetails

const val DEFAULT_DEBOUNCE_DELAY = 300L
private const val GROUP_SIZE = 3

fun TextView.setPrettyHtmlByTags(html: String) {
    val doc = Jsoup.parseBodyFragment(html)
    val out = SpannableStringBuilder()

    val renderer = HtmlTagRenderer(
        textView = this,
        out = out
    )

    doc.body().childNodes().forEach(renderer::render)

    text = out
}

private class HtmlTagRenderer(
    private val textView: TextView,
    private val out: SpannableStringBuilder,
) {
    fun render(node: Node) {
        when (node) {
            is TextNode -> appendNormalizedText(node.text())
            is Element -> renderElement(node)
            else -> renderChildren(node)
        }
    }

    private fun renderElement(element: Element) {
        when (element.tagName().lowercase()) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> renderHeader(element)
            "p" -> renderParagraph(element)
            "ul" -> renderUnorderedList(element)
            "br" -> appendNewLine()
            else -> renderGenericBlock(element)
        }
    }

    private fun renderHeader(element: Element) {
        val level = element.tagName().substring(1).toIntOrNull() ?: DEFAULT_HEADER_LEVEL

        val start = out.length
        appendNormalizedText(element.text())
        val end = out.length

        applyHeaderSpan(start, end, level)
        // если нужно — можно добавить перевод строки после заголовка:
        // appendNewLine()
    }

    private fun renderParagraph(element: Element) {
        if (out.isNotEmpty()) appendNewLine()
        renderChildren(element)
        appendNewLine()
    }

    private fun renderUnorderedList(element: Element) {
        if (out.isNotEmpty()) appendNewLine()

        element.children()
            .filter { it.tagName().equals("li", ignoreCase = true) }
            .forEach { li ->
                out.append(BULLET_PREFIX)
                appendNormalizedText(li.text())
                appendNewLine()
            }
    }

    private fun renderGenericBlock(element: Element) {
        if (out.isNotEmpty()) appendNewLine()
        renderChildren(element)
    }

    private fun renderChildren(node: Node) {
        node.childNodes().forEach(::render)
    }

    private fun appendNormalizedText(text: String) {
        val normalized = text
            .replace(NBSP, ' ')
            .trim()

        if (normalized.isNotBlank()) out.append(normalized)
    }

    private fun appendNewLine(count: Int = 1) {
        repeat(count) { out.append('\n') }
    }

    private fun applyHeaderSpan(start: Int, end: Int, level: Int) {
        val sizePx = textView.resources.getDimensionPixelSize(levelToDimen(level))

        out.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        out.setSpan(
            AbsoluteSizeSpan(sizePx),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    @DimenRes
    private fun levelToDimen(level: Int): Int = when (level) {
        LEVEL_ONE -> R.dimen.text_size_xl
        LEVEL_TWO -> R.dimen.text_size_lg
        LEVEL_THREE -> R.dimen.text_size_md
        else -> R.dimen.text_size_sm
    }

    private companion object {
        const val DEFAULT_HEADER_LEVEL = 3
        const val NBSP = '\u00A0'
        const val BULLET_PREFIX = "• "
        const val LEVEL_ONE = 1
        const val LEVEL_TWO = 2
        const val LEVEL_THREE = 3
    }
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

fun formatSalary(salary: VacancyCardSalary?, resources: Resources): String {
    val fromText = resources.getString(R.string.salary_from)
    val toText = resources.getString(R.string.salary_to)
    val currencyFormat = when (val currency = salary?.currency.orEmpty()) {
        "RUR" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        "KZT" -> "₸"
        else -> currency
    }

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
            "$fromText ${salary.from.formatWithSpaces()} $toText ${salary.to.formatWithSpaces()} $currencyFormat"
        }

        salary.from != null -> {
            "$fromText ${salary.from.formatWithSpaces()} $currencyFormat"
        }

        salary.to != null -> {
            "$toText ${salary.to.formatWithSpaces()} $currencyFormat"
        }

        else -> resources.getString(R.string.salary_not_specified)
    }
}

/** Загрузка svg иконок */
fun loadSvgInto(url: String, image: ImageView) {
    image.load(url) {
        placeholder(R.drawable.vacancy_placeholder)
        error(R.drawable.vacancy_placeholder)
        fallback(R.drawable.vacancy_placeholder) // Для пустой строки
        addHeader("Accept", "image/svg+xml")
        decoderFactory(SvgDecoder.Factory())
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

/**
 * Функция для создания заголовка/названия для вакансии
 */
fun createTitleVacancy(name: String, city: String?): String {
    var title = name
    if (city != null) title += ", $city"

    return title
}

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
