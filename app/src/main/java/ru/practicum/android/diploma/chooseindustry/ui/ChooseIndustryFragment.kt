package ru.practicum.android.diploma.chooseindustry.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.chooseindustry.model.ChooseIndustryState
import ru.practicum.android.diploma.chooseindustry.model.RecyclerState
import ru.practicum.android.diploma.chooseindustry.presentation.ChooseIndustryAdapter
import ru.practicum.android.diploma.chooseindustry.presentation.ChooseIndustryViewModel
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.ui.state.PlaceholderType
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding

class ChooseIndustryFragment : Fragment() {

    private var _binding: FragmentChooseIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseIndustryViewModel by viewModel()

    private val adapter = ChooseIndustryAdapter { selectIndustry(it) }

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

        binding.recyclerList.adapter = adapter
        viewModel.observePage().observe(viewLifecycleOwner) {
            renderActivity(it)
        }
        init()
        initToolbar()
        initSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")

        if (id != null && name != null) {
            viewModel.selectIndustry(VacancyDetails.Industry(id = id, name = name))
            adapter.setDefaultId(id)
        }

        viewModel.loadIndustries()

        // Кнопка "Выбрать"
        binding.buttonApply.setOnClickListener {
            val selectedItem = viewModel.getSelectItem()

            if (selectedItem != null) {
                findNavController().navigate(
                    R.id.action_chooseIndustryFragment_to_filtersFragment,
                    Bundle().apply {
                        putInt("id", selectedItem.id)
                        putString("name", selectedItem.name)
                    }
                )
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initSearch() {
        binding.search.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearch(text.toString())
        }
    }

    private fun selectIndustry(industry: VacancyDetails.Industry) {
        viewModel.selectIndustry(industry)
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholder.placeholderInfo.visibility = View.GONE
        binding.recyclerList.visibility = View.GONE
        binding.buttonApply.visibility = View.GONE
    }

    private fun showError(serverCode: String) {
        binding.progressBar.visibility = View.GONE
        binding.placeholder.placeholderInfo.visibility = View.VISIBLE
        binding.recyclerList.visibility = View.GONE
        binding.buttonApply.visibility = View.GONE

        val message = when (serverCode) {
            "-1" -> getString(R.string.no_internet)
            else -> getString(R.string.search_industry_error_list)
        }

        initPlaceholder(PlaceholderType.ERROR, message)
    }

    private fun showSuccess(recyclerState: RecyclerState) {
        val filterList = recyclerState.list.filter {
            it.name.contains(recyclerState.filter, ignoreCase = true)
        }

        if (filterList.isEmpty()) {
            binding.progressBar.visibility = View.GONE
            binding.placeholder.placeholderInfo.visibility = View.VISIBLE
            binding.recyclerList.visibility = View.GONE
            binding.buttonApply.visibility = View.GONE

            initPlaceholder(PlaceholderType.EMPTY, getString(R.string.search_industry_empty_list))
        } else {
            binding.progressBar.visibility = View.GONE
            binding.placeholder.placeholderInfo.visibility = View.GONE
            binding.recyclerList.visibility = View.VISIBLE
            binding.buttonApply.isVisible = recyclerState.selectItem != null
        }

        adapter.update(filterList)
    }

    private fun initPlaceholder(type: PlaceholderType, message: String) {
        val imgElement = binding.placeholder.placeholderInfoImg
        val textElement = binding.placeholder.placeholderInfoText
        val imgUrl = when (type) {
            PlaceholderType.EMPTY -> R.drawable.favorites_empty_list
            PlaceholderType.ERROR -> R.drawable.no_internet
            else -> R.drawable.favorites_error_load
        }

        Glide.with(this)
            .load(imgUrl)
            .into(imgElement)

        textElement.text = message
    }

    /** Рендер состояния страницы */
    private fun renderActivity(state: ChooseIndustryState) {
        when (state) {
            is ChooseIndustryState.Loading -> showLoading()
            is ChooseIndustryState.Error -> showError(state.serverCode)
            is ChooseIndustryState.Success -> showSuccess(state.recyclerState)
        }
    }
}
