package ru.practicum.android.diploma.settingsfilter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.settingsfilter.ui.presentation.FiltersViewModel
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_AREA
import ru.practicum.android.diploma.vacancysearch.ui.VacancySearchFragment.Companion.KEY_INDUSTRY
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
        // Тестовый пример. Можно не передавать Bundle
//        binding.button.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_filtersFragment_to_chooseIndustryFragment
//                Bundle().apply {
//                    putInt("id", 15)
//                    putString("name", "Автомобильный бизнес")
//                }
//            )
//        }

        initFilters()
        setupListeners()
    }

    private fun initFilters() {
        val args = arguments ?: return

        setFilters(args)
        with(binding) {
            val currentFilters = viewModel.getCurrentFilters()
            currentFilters?.salary?.let {
                desiredSalary.setText(it.toString())
                salaryHint.setTextColor(resources.getColor(R.color.black))
            }
            check.isChecked = currentFilters?.showSalary == true
        }
    }

    private fun setFilters(args: Bundle) {
        viewModel.setFilters(
            SearchFilters(
                areaCountry = args.getInt(KEY_AREA)
                    .takeIf { it != 0 }
                    ?.let { SearchFilters.AreaCountry(it, "") },
                industry = args.getInt(KEY_INDUSTRY)
                    .takeIf { it != 0 }
                    ?.let { SearchFilters.Industry(it, "") },
                salary = args.getInt(KEY_SALARY).takeIf { it != 0 },
                showSalary = args.getBoolean(KEY_ONLY_WITH_SALARY, false)
            )
        )
    }

    private fun setupListeners() {
        configureApplyChangesLayoutVisibility(isInitial = true)
        with(binding) {
            setToolbarNavigationListener()
            setDesiredSalaryFocusListener()
            setOnDesiredSalaryChangeListener()
            setApplyButtonListener()
            setDiscardButtonListener()
            setCheckButtonListener()
        }
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
            )
        }
    }

    private fun FragmentFiltersBinding.setDiscardButtonListener() {
        discardButton.setOnClickListener {
            viewModel.resetFilters()
            with(binding) {
                desiredSalary.setText(null)
                check.isChecked = false
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
            val navController = findNavController()

            val result = Bundle().apply {
                viewModel.getCurrentFilters()?.let { filters ->
                    filters.areaCountry?.id?.let { putInt(KEY_AREA, it) }
                    filters.industry?.id?.let { putInt(KEY_INDUSTRY, it) }
                    filters.salary?.let { putInt(KEY_SALARY, it) }
                    filters.showSalary?.let { putBoolean(KEY_ONLY_WITH_SALARY, it) }
                }
            }

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("filters_result", result)

            navController.popBackStack()
        }
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
                    filledIndustryText.text.isNotEmpty()
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
