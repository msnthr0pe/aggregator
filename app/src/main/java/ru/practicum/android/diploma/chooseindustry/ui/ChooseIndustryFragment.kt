package ru.practicum.android.diploma.chooseindustry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.chooseindustry.model.ChooseIndustryState
import ru.practicum.android.diploma.chooseindustry.presentation.ChooseIndustryViewModel
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding

class ChooseIndustryFragment : Fragment() {

    private var _binding: FragmentChooseIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseIndustryViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
//        val testData = VacancyDetails.Industry(id = "")
        // VacancyDetails.Industry
//        val test = requireArguments()
//
//        Log.i("TEST", "TEST")
    }

    private fun showEmpty() {}

    private fun showLoading() {}

    private fun showError(serverCode: String) {}

    private fun showSuccess(list: List<VacancyDetails.Industry>) {}

    /** Рендер состояния страницы */
    private fun renderActivity(state: ChooseIndustryState) {
        when (state) {
            is ChooseIndustryState.Empty -> showEmpty()
            is ChooseIndustryState.Loading -> showLoading()
            is ChooseIndustryState.Error -> showError(state.serverCode)
            is ChooseIndustryState.Success -> showSuccess(state.items)
        }
    }
}
