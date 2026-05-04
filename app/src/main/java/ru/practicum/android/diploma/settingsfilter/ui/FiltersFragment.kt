package ru.practicum.android.diploma.settingsfilter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.settingsfilter.ui.presentation.FiltersViewModel
import ru.practicum.android.diploma.settingsfilter.ui.util.getFilters
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_AREA
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_ID
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY_NAME
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_ONLY_WITH_SALARY
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_SALARY

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilters()
        setupListeners()
    }

    private fun initFilters() {
        val args = arguments ?: return

        setFilters(args)
        with(binding) {
            val currentFilters = viewModel.getCurrentFilters()
            currentFilters?.industry?.let {
                setIndustryLayoutMode(isEmpty = false)
                filledIndustryText.text = it.name
            }
            currentFilters?.salary?.let {
                desiredSalary.setText(it.toString())
                salaryHint.setTextColor(resources.getColor(R.color.black))
            }
            check.isChecked = currentFilters?.showSalary == true
        }
    }

    private fun setFilters(args: Bundle) {
        viewModel.setFilters(
            getFilters(args)
        )
    }

    private fun setupListeners() {
        configureApplyChangesLayoutVisibility(isInitial = true)
        setBackPressedListener()
        with(binding) {
            setToolbarNavigationListener()
            setDesiredSalaryFocusListener()
            setOnDesiredSalaryChangeListener()
            setApplyButtonListener()
            setDiscardButtonListener()
            setCheckButtonListener()
            setIndustryButtonListener()
        }
    }

    private fun FragmentFiltersBinding.setIndustryButtonListener() {
        industryButton.setOnClickListener {
            sendResultAndOpenIndustries()
        }
        filledIndustryIconCross.setOnClickListener {
            filledIndustryText.text = null
            setIndustryLayoutMode(isEmpty = true)
            configureApplyChangesLayoutVisibility()
        }
    }

    private fun FragmentFiltersBinding.setIndustryLayoutMode(isEmpty: Boolean) {
        emptyIndustryText.isVisible = isEmpty
        emptyIndustryIcon.isVisible = isEmpty
        filledIndustryText.isVisible = !isEmpty
        filledIndustryHint.isVisible = !isEmpty
        filledIndustryIconCross.isVisible = !isEmpty
    }

    private fun FragmentFiltersBinding.setApplyButtonListener() {
        applyButton.setOnClickListener {
            configureApplyChangesLayoutVisibility(forceHide = true)
            val desiredSalary = if (desiredSalary.text?.isNotEmpty() == true) {
                desiredSalary.text.toString().toInt()
            } else {
                null
            }
            viewModel.updateFilters(
                salary = desiredSalary,
                showSalary = check.isChecked,
                clearIndustrySelection = filledIndustryText.text.isEmpty()
            )
            sendResultAndClose()
        }
    }

    private fun FragmentFiltersBinding.setDiscardButtonListener() {
        discardButton.setOnClickListener {
            viewModel.resetFilters()
            with(binding) {
                desiredSalary.setText(null)
                check.isChecked = false
                filledIndustryText.text = null
                setIndustryLayoutMode(isEmpty = true)
            }
            configureApplyChangesLayoutVisibility(forceHide = true)
        }
    }

    private fun FragmentFiltersBinding.setCheckButtonListener() {
        check.setOnCheckedChangeListener { _, _ ->
            configureApplyChangesLayoutVisibility()
        }
    }

    private fun FragmentFiltersBinding.setToolbarNavigationListener() {
        toolbar.setNavigationOnClickListener {
            viewModel.resetFilters()
            sendResultAndClose()
        }
    }

    private fun setBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.resetFilters()
                    sendResultAndClose()
                }
            }
        )
    }

    private fun sendResultAndClose() {
        val navController = findNavController()

        val result = getResultFilterBundle()

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("filters_result", result)

        navController.popBackStack()
    }

    private fun getResultFilterBundle(): Bundle {
        return Bundle().apply {
            viewModel.getCurrentFilters()?.let { filters ->
                filters.areaCountry?.id?.let { putInt(KEY_AREA, it) }
                filters.industry?.id?.let { putInt(KEY_INDUSTRY_ID, it) }
                filters.industry?.name?.let { putString(KEY_INDUSTRY_NAME, it) }
                filters.salary?.let { putInt(KEY_SALARY, it) }
                filters.showSalary?.let { putBoolean(KEY_ONLY_WITH_SALARY, it) }
            }
        }
    }

    private fun sendResultAndOpenIndustries() {
        val navController = findNavController()

        navController.navigate(
            R.id.action_filtersFragment_to_chooseIndustryFragment,
            getResultFilterBundle(),
        )
    }

    private fun configureApplyChangesLayoutVisibility(forceHide: Boolean = false, isInitial: Boolean = false) {
        binding.setLayoutConfirmDiscardVisibility(forceHide, isInitial)
    }

    private fun FragmentFiltersBinding.setLayoutConfirmDiscardVisibility(
        forceHide: Boolean,
        isInitial: Boolean,
    ) {
        val currentFilters = viewModel.getCurrentFilters()
        val showSalary = currentFilters?.showSalary ?: false
        val salary = if (desiredSalary.text?.isNotEmpty() == true) {
            desiredSalary.text.toString().toInt()
        } else {
            0
        }
        layoutConfirmDiscard.isVisible = if (forceHide) {
            false
        } else {
            if (isInitial) {
                desiredSalary.text?.isNotEmpty() == true ||
                    check.isChecked ||
                    filledIndustryText.text.isNotEmpty()
            } else {
                salary != currentFilters?.salary ||
                    check.isChecked != showSalary ||
                    filledIndustryText.text != currentFilters.industry?.name
            }
        }
    }

    private fun FragmentFiltersBinding.setDesiredSalaryFocusListener() {
        desiredSalary.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                salaryHint.setTextColor(resources.getColor(R.color.blue))
            } else {
                val salaryHintColorId = if (desiredSalary.text?.isNotEmpty() == true) {
                    R.color.black
                } else {
                    R.color.gray
                }
                salaryHint.setTextColor(resources.getColor(salaryHintColorId))
            }
        }
    }

    private fun FragmentFiltersBinding.setOnDesiredSalaryChangeListener() {
        desiredSalary.doOnTextChanged { text, _, _, _ ->
            configureApplyChangesLayoutVisibility()
            if (text?.isEmpty() == true && !desiredSalary.hasFocus()) {
                salaryHint.setTextColor(resources.getColor(R.color.gray))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
