package ru.practicum.android.diploma.vacancy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.ui.root.RootActivity
import ru.practicum.android.diploma.core.ui.state.PlaceholderType
import ru.practicum.android.diploma.core.util.formatSalary
import ru.practicum.android.diploma.core.util.openDialer
import ru.practicum.android.diploma.core.util.sendEmail
import ru.practicum.android.diploma.core.util.shareVacancy
import ru.practicum.android.diploma.core.util.tag
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.vacancy.model.VacancyPageState
import ru.practicum.android.diploma.vacancy.model.VacancyState
import ru.practicum.android.diploma.vacancy.presentation.VacancyViewModel
import ru.practicum.android.diploma.vacancy.presentation.setContactsClickable
import ru.practicum.android.diploma.vacancy.presentation.updateAreaName
import ru.practicum.android.diploma.vacancy.presentation.updateCompanyLogo
import ru.practicum.android.diploma.vacancy.presentation.updateDescription
import ru.practicum.android.diploma.vacancy.presentation.updateEmployer
import ru.practicum.android.diploma.vacancy.presentation.updateSchedule

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

        viewModel.observeVacancyDetails().observe(viewLifecycleOwner) {
            renderActivity(it)
        }

        init()
        initToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        val vacancyId = requireArguments().getString("ID")

        vacancyId?.let {
            viewModel.init(it)
        }
    }

    private fun initToolbar() {
        val menuShare = binding.toolbar.menu.findItem(R.id.toolbar_share)
        val menuLike = binding.toolbar.menu.findItem(R.id.toolbar_like)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        menuShare.setOnMenuItemClickListener {
            val vacancyDetails = viewModel.getVacancyDetails()

            if (vacancyDetails != null) {
                rootActivity.shareVacancy(vacancyDetails.url)
            }

            true
        }

        menuLike.setOnMenuItemClickListener {
            viewModel.toggleRequestFavorite()
            true
        }
    }

    private fun updateVacancyDetails(vacancyDetails: VacancyDetails) {
        with(binding) {
            vacancyTitle.text = vacancyDetails.name
            vacancySubtitle.text = formatSalary(vacancyDetails.salary, binding.root.resources)
            vacancyCardItem.vacancyItemTitle.updateEmployer(vacancyDetails.employer.name)
            vacancyCardItem.vacancyItemCompany.updateAreaName(vacancyDetails)
            vacancyCardItem.vacancyItemImg.updateCompanyLogo(vacancyDetails.employer.logo)
            schedule.updateSchedule(vacancyDetails)
            vacancyDescription.updateDescription(vacancyDetails.description)
            updateRequiredExperience(vacancyDetails.experience)
            updateRequiredSkills(vacancyDetails.skills)
            updateContacts(vacancyDetails.contacts)
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

    private fun changeFavoriteIcon(isFavorite: Boolean) {
        val menuLike = binding.toolbar.menu.findItem(R.id.toolbar_like)

        if (isFavorite) {
            menuLike.setIcon(R.drawable.favorites_ic)
        } else {
            menuLike.setIcon(R.drawable.like)
        }
    }

    private fun showContent(vacancyState: VacancyState) {
        binding.progressBar.isVisible = false
        binding.vacancyContents.isVisible = true
        binding.placeholder.placeholderInfo.isVisible = false
        binding.toolbar.menu.findItem(R.id.toolbar_share).isVisible = true
        binding.toolbar.menu.findItem(R.id.toolbar_like).isVisible = true

        changeFavoriteIcon(vacancyState.isFavorite)

        if (vacancyState.vacancyDetails != null) {
            updateVacancyDetails(vacancyState.vacancyDetails)
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.vacancyContents.isVisible = false
        binding.placeholder.placeholderInfo.isVisible = false
        binding.toolbar.menu.findItem(R.id.toolbar_share).isVisible = false
        binding.toolbar.menu.findItem(R.id.toolbar_like).isVisible = false
    }

    private fun showError(serverCode: String) {
        binding.progressBar.isVisible = false
        binding.vacancyContents.isVisible = false
        binding.placeholder.placeholderInfo.isVisible = true
        binding.toolbar.menu.findItem(R.id.toolbar_share).isVisible = false
        binding.toolbar.menu.findItem(R.id.toolbar_like).isVisible = false

        val message = when (serverCode) {
            "-1" -> getString(R.string.no_internet)
            "404" -> getString(R.string.vacancy_empty)
            else -> getString(R.string.error)
        }

        initPlaceholder(PlaceholderType.ERROR, message)
    }

    private fun initPlaceholder(type: PlaceholderType, message: String) {
        val imgElement = binding.placeholder.placeholderInfoImg
        val textElement = binding.placeholder.placeholderInfoText
        val imgUrl = when (type) {
            PlaceholderType.NOTHING -> R.drawable.placeholder
            PlaceholderType.ERROR -> R.drawable.server_error_on_vacancy
            PlaceholderType.EMPTY -> R.drawable.vacancy_not_found
        }

        Glide.with(this)
            .load(imgUrl)
            .into(imgElement)

        textElement.text = message
    }

    private fun renderActivity(state: VacancyPageState) {
        when (state) {
            is VacancyPageState.Loading -> showLoading()
            is VacancyPageState.Error -> showError(state.serverCode)
            is VacancyPageState.Success -> showContent(state.vacancyState)
        }
    }
}
