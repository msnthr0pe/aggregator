package ru.practicum.android.diploma.vacancy.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.core.util.tag
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.ui.root.RootActivity
import ru.practicum.android.diploma.core.util.loadSvgInto
import ru.practicum.android.diploma.core.util.openDialer
import ru.practicum.android.diploma.core.util.setPrettyHtmlByTags
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.vacancy.presentation.VacancyViewModel

class VacancyFragment : Fragment() {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val rootActivity by lazy { requireActivity() as RootActivity }

    private val viewModel: VacancyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        // для теста
        viewModel.requestVacancyDetails("0003911b-6d19-3d68-bcc5-576fe288f2b9")
        viewModel.observeVacancyDetails().observe(viewLifecycleOwner) { vacancyDetails ->
            vacancyDetails?.let {
                updateVacancyDetails(it)
            } ?: updateLoader(isLoading = true)
            tag(vacancyDetails)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setOnClickListeners()
    }

    private fun setupUi() {
        with(binding) {
            vacancyCardItem.vacancyItemSalary.isVisible = false
            toolbar.title.text = rootActivity.getString(R.string.vacancy_toolbar_title)
            toolbar.firstToolbarAction.setImageResource(R.drawable.share)
            toolbar.secondToolbarAction.setImageResource(R.drawable.like)
        }
    }

    private fun updateVacancyDetails(vacancyDetails: VacancyDetails) {
        updateLoader(isLoading = false)
        with(binding) {
            vacancyTitle.text = vacancyDetails.name

            vacancyDetails.salary?.let {
                val currency = it.currency
                vacancySubtitle.text = getVacancySalary(it, currency.orEmpty())
            } ?: run {
                vacancySubtitle.setText(R.string.vacancy_no_salary_bounds)
            }

            // Карточка используется в разных экранах
            // и в разных экранах одно поле может отображать разные данные
            // я пока что не хочу переименовывать id элементов карточки, поэтому
            // присвоение будет выглядеть криво
            vacancyCardItem.vacancyItemTitle.text = vacancyDetails.employer.name

            // Здесь так же
            vacancyCardItem.vacancyItemCompany.text =
                vacancyDetails.address?.raw ?: vacancyDetails.area.name

            tag(vacancyDetails.employer.logo)
            loadSvgInto(vacancyDetails.employer.logo, vacancyCardItem.vacancyItemImg)

            requiredExperienceTitle.visibility = if (vacancyDetails.experience != null) {
                View.VISIBLE
            } else {
                requiredExperience.visibility = View.GONE
                View.GONE
            }
            vacancyDetails.experience?.let {
                requiredExperience.text = it.name
            }

            val requiredSchedule = vacancyDetails.employment?.name?.let{"$it, "} + vacancyDetails.schedule?.name
            schedule.visibility = if (requiredSchedule.isEmpty()) View.GONE else View.VISIBLE
            schedule.text = requiredSchedule

            vacancyDescription.setPrettyHtmlByTags(vacancyDetails.description)

            val showSkills = vacancyDetails.skills.isNotEmpty()
            skillsTitle.visibility = if (showSkills) View.VISIBLE else View.GONE
            skills.visibility = if (showSkills) View.VISIBLE else View.GONE
            var skillsText = ""
            vacancyDetails.skills.forEach {
                skillsText += "• $it \n"
            }
            skills.text = skillsText

            val vacancyContacts = vacancyDetails.contacts?.phones
            contactsTitle.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            contacts.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            contactName.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            vacancyContacts?.let {
                contactName.text = vacancyDetails.contacts.name

                contacts.setPhonesClickable(
                    phones = it,
                    onPhoneClick = { phone ->
                        rootActivity.openDialer(phone)
                        tag(phone)
                    }
                )
            }
        }
    }

    fun TextView.setPhonesClickable(
        phones: List<VacancyDetails.Phone>,
        onPhoneClick: (String) -> Unit
    ) {
        val ssb = SpannableStringBuilder()

        phones.forEachIndexed { index, phone ->
            val start = ssb.length
            ssb.append(phone.formatted)
            phone.comment?.let {
                ssb.append(it)
            }
            val end = ssb.length

            ssb.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) = onPhoneClick(phone.formatted)
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (index != phones.lastIndex) ssb.append("\n\n")
        }

        text = ssb
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }

    private fun getVacancySalary(salary: VacancyDetails.Salary, currency: String): String {
        return when {
            salary.from != null && salary.to != null ->
                rootActivity.getString(
                    R.string.vacancy_salary_bounds_from_to,
                    salary.from.toString(),
                    currency,
                    salary.to.toString(),
                    currency,
                )
            salary.from == null && salary.to != null ->
                rootActivity.getString(
                    R.string.vacancy_salary_bounds_to,
                    salary.to.toString(),
                    currency,
                )
            salary.from != null && salary.to == null ->
                rootActivity.getString(
                    R.string.vacancy_salary_bounds_from,
                    salary.from.toString(),
                    currency,
                )
            else -> rootActivity.getString(R.string.vacancy_no_salary_bounds)
        }
    }

    private fun updateLoader(isLoading: Boolean) {
        with(binding) {
            progressBar.isVisible = isLoading
            toolbar.firstToolbarAction.visibility = if(isLoading) View.GONE else View.VISIBLE
            toolbar.secondToolbarAction.visibility = if(isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setOnClickListeners() {
        binding.toolbar.arrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
