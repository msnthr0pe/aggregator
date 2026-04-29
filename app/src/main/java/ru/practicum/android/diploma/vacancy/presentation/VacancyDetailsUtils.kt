package ru.practicum.android.diploma.vacancy.presentation

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.util.loadSvgInto
import ru.practicum.android.diploma.core.util.setPrettyHtmlByTags

fun TextView.updateEmployer(employerName: String) {
    // Карточка используется в разных экранах
    // и в разных экранах одно поле может отображать разные данные
    // я пока что не хочу переименовывать id элементов карточки, поэтому
    // присвоение будет выглядеть криво
    text = employerName
}

fun TextView.updateAreaName(vacancyDetails: VacancyDetails) {
    // Здесь так же
    text = vacancyDetails.address?.raw ?: vacancyDetails.area.name
}

fun ImageView.updateCompanyLogo(logoPath: String) {
    loadSvgInto(logoPath, this)
}

fun TextView.updateSchedule(vacancyDetails: VacancyDetails) {
    val requiredSchedule = vacancyDetails.employment?.name?.let { "$it, " } + vacancyDetails.schedule?.name
    visibility = if (requiredSchedule.isEmpty()) View.GONE else View.VISIBLE
    text = requiredSchedule
}

fun TextView.updateDescription(description: String) {
    setPrettyHtmlByTags(description)
}

fun TextView.setContactsClickable(
    context: Context,
    phones: List<VacancyDetails.Phone>,
    email: String?,
    onPhoneClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
) {
    val ssb = SpannableStringBuilder()

    fun addClickable(
        value: String,
        onClick: (String) -> Unit
    ) {
        val start = ssb.length
        ssb.append(value)
        val end = ssb.length

        ssb.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) = onClick(value)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = context.getColor(R.color.blue)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    phones.forEachIndexed { index, phone ->
        addClickable(phone.formatted, onPhoneClick)

        phone.comment?.takeIf { it.isNotBlank() }?.let { comment ->
            ssb.append(" ")
            ssb.append(comment)
        }

        if (index != phones.lastIndex || !email.isNullOrBlank()) ssb.append("\n\n")
    }

    email?.trim()?.takeIf { it.isNotBlank() }?.let { mail ->
        addClickable(mail, onEmailClick)
    }

    text = ssb
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}
