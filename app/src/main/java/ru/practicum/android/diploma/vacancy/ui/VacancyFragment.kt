package ru.practicum.android.diploma.vacancy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.ui.root.RootActivity
import ru.practicum.android.diploma.core.util.formatSalary
import ru.practicum.android.diploma.core.util.hasNetwork
import ru.practicum.android.diploma.core.util.openDialer
import ru.practicum.android.diploma.core.util.sendEmail
import ru.practicum.android.diploma.core.util.shareVacancy
import ru.practicum.android.diploma.core.util.tag
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.vacancy.presentation.VacancyViewModel
import ru.practicum.android.diploma.vacancy.presentation.setContactsClickable
import ru.practicum.android.diploma.vacancy.presentation.updateAreaName
import ru.practicum.android.diploma.vacancy.presentation.updateCompanyLogo
import ru.practicum.android.diploma.vacancy.presentation.updateDescription
import ru.practicum.android.diploma.vacancy.presentation.updateEmployer
import ru.practicum.android.diploma.vacancy.presentation.updateSchedule
import ru.practicum.android.diploma.vacancy.presentation.updateTitle

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setOnClickListeners()
        setViewModelObserver()
        requestVacancyDetails()
    }

    private fun setViewModelObserver() {
        viewModel.observeVacancyDetails().observe(viewLifecycleOwner) { vacancyState ->
            tag(vacancyState)
            vacancyState.vacancyDetails?.let {
                updateVacancyDetails(it)
                binding.toolbar.firstToolbarAction.visibility = View.VISIBLE
                binding.toolbar.secondToolbarAction.visibility = View.VISIBLE
            } ?: updatePlaceholderState(isLoading = true, isServerError = vacancyState.isServerError)
        }
    }

    private fun setupUi() {
        with(binding) {
            vacancyCardItem.vacancyItemSalary.isVisible = false
            toolbar.title.text = rootActivity.getString(R.string.vacancy_toolbar_title)
            toolbar.firstToolbarAction.apply {
                visibility = View.GONE
                setImageResource(R.drawable.share)
            }
            toolbar.secondToolbarAction.apply {
                visibility = View.GONE
                setImageResource(R.drawable.like)
            }
        }
    }

    private fun requestVacancyDetails() {
        val vacancyId = requireArguments().getString("ID")
        vacancyId?.let {
            viewModel.requestVacancyDetails(it)
        }
    }

    private fun updateVacancyDetails(vacancyDetails: VacancyDetails) {
        updatePlaceholderState(isLoading = false)
        with(binding) {
            vacancyTitle.updateTitle(vacancyDetails.name)
            vacancySubtitle.text = formatSalary(vacancyDetails.salary, binding.root.resources)
            vacancyCardItem.vacancyItemTitle.updateEmployer(vacancyDetails.employer.name)
            vacancyCardItem.vacancyItemCompany.updateAreaName(vacancyDetails)
            vacancyCardItem.vacancyItemImg.updateCompanyLogo(vacancyDetails.employer.logo)
            schedule.updateSchedule(vacancyDetails)
            vacancyDescription.updateDescription(vacancyDetails.description)
            updateRequiredExperience(vacancyDetails.experience)
            updateRequiredSkills(vacancyDetails.skills)
            updateContacts(vacancyDetails.contacts)
            updateToolbarActions(vacancyDetails)
        }
    }

    private fun updateRequiredExperience(experience: VacancyDetails.Experience?) {
        binding.requiredExperienceTitle.visibility = if (experience != null) {
            View.VISIBLE
        } else {
            binding.requiredExperience.visibility = View.GONE
            View.GONE
        }
        experience?.let {
            binding.requiredExperience.text = it.name
        }
    }

    private fun updateRequiredSkills(skills: List<String>) {
        val showSkills = skills.isNotEmpty()
        binding.skillsTitle.visibility = if (showSkills) View.VISIBLE else View.GONE
        binding.skills.visibility = if (showSkills) View.VISIBLE else View.GONE
        var skillsText = ""
        skills.forEach {
            skillsText += "• $it \n"
        }
        binding.skills.text = skillsText
    }

    private fun updateContacts(contactsForUpdate: VacancyDetails.Contacts?) {
        with(binding) {
            val vacancyContacts = contactsForUpdate
            contactsTitle.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            contacts.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            contactName.visibility = if (vacancyContacts != null) View.VISIBLE else View.GONE
            vacancyContacts?.let {
                contactName.text = contactsForUpdate.name

                contacts.setContactsClickable(
                    context = rootActivity,
                    phones = it.phones,
                    email = it.email,
                    onPhoneClick = { phone ->
                        rootActivity.openDialer(phone)
                        tag(phone)
                    },
                    onEmailClick = { email ->
                        rootActivity.sendEmail(email)
                        tag(email)
                    }
                )
            }
        }
    }

    private fun updateToolbarActions(vacancyDetails: VacancyDetails) {
        binding.toolbar.firstToolbarAction.setOnClickListener {
            rootActivity.shareVacancy(vacancyDetails.url)
        }
    }

    private fun updatePlaceholderState(isLoading: Boolean, isServerError: Boolean = false) {
        with(binding) {
            val hasNetwork = rootActivity.hasNetwork()
            progressBar.isVisible = isLoading && hasNetwork

            if (!hasNetwork) {
                showNoInternetPlaceholder()
            } else if (isServerError) {
                showServerErrorPlaceholder()
            }
        }
    }

    private fun showNoInternetPlaceholder() {
        with(binding) {
            errorPlaceholderLayout.isVisible = true
            errorVacancyPlaceholder.setImageResource(R.drawable.no_internet)
            errorVacancyPlaceholderText.setText(R.string.no_internet)
        }
    }

    private fun showServerErrorPlaceholder() {
        with(binding) {
            errorPlaceholderLayout.isVisible = true
            errorVacancyPlaceholder.setImageResource(R.drawable.server_error_on_vacancy)
            errorVacancyPlaceholderText.setText(R.string.server_error)
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
