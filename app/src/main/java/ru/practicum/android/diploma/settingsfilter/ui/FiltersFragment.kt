package ru.practicum.android.diploma.settingsfilter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

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

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setDesiredSalaryFocusListener()
            setOnDesiredSalaryChangeListener()
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
