package ru.practicum.android.diploma.vacancysearch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.SearchFilters
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.core.ui.root.RootActivity
import ru.practicum.android.diploma.core.ui.state.PlaceholderType
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.vacancy.ui.VacancyFragment
import ru.practicum.android.diploma.vacancysearch.ui.state.VacancySearchState

class VacancySearchFragment : Fragment() {

    companion object {
        const val KEY_AREA = "area"
        const val KEY_INDUSTRY = "industry"
        const val KEY_SALARY = "salary"
        const val KEY_ONLY_WITH_SALARY = "only_with_salary"
    }

    private val viewModel by viewModel<VacancySearchViewModel>()
    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!
    private var vacancyAdapter = VacancyAdapter { selectVacancyHandler(it) }
    private val rootActivity by lazy { requireActivity() as RootActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerList.apply {
            adapter = vacancyAdapter.withLoadStateFooter(
                footer = VacancyLoadStateAdapter { vacancyAdapter.retry() }
            )
            addItemDecoration(
                FirstItemTopMarginDecoration(resources.getDimensionPixelSize(R.dimen.spacing_xxxl))
            )
        }

        viewModel.observePage().observe(viewLifecycleOwner) {
            renderActivity(it)
            updateFilterIcon()
        }

        val toolbar = binding.btnBack.menu.findItem(R.id.toolbar_filter)
        toolbar.setOnMenuItemClickListener {
            findNavController().navigate(
                R.id.action_vacancySearchFragment_to_filtersFragment,
                Bundle().apply {
                    viewModel.getCurrentFilters()?.let { filters ->
                        filters.areaCountry?.id?.let { putInt(KEY_AREA, it) }
                        filters.industry?.id?.let { putInt(KEY_INDUSTRY, it) }
                        filters.salary?.let { putInt(KEY_SALARY, it) }
                        filters.showSalary?.let { putBoolean(KEY_ONLY_WITH_SALARY, it) }
                    }
                }
            )
            true
        }

        initFilters()
        initSearch()
        initVacancyList()
    }

    override fun onResume() {
        super.onResume()
        updateFilterIcon()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Инициализация поисковика */
    private fun initSearch() {
        _binding?.search?.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchDebounce(text.toString().trim())
        }

        _binding?.searchWrapper?.setEndIconOnClickListener {
            _binding?.search?.text?.clear()
        }
    }

    /** Инициализация списка вакансий */
    private fun initVacancyList() {
        // Сбор данных
        lifecycleScope.launch {
            viewModel.items.collectLatest { pagingData ->
                vacancyAdapter.submitData(pagingData)
            }
        }

        // Обработка для заглушек
        lifecycleScope.launch {
            vacancyAdapter.loadStateFlow.collectLatest { loadStates ->
                val isFirstLoading = loadStates.refresh is LoadState.Loading
                val isListEmpty = vacancyAdapter.itemCount == 0
                val hasError = loadStates.refresh is LoadState.Error

                if (hasError) {
                    val refreshState = loadStates.refresh
                    var errorMessage = "500"

                    if (refreshState is LoadState.Error) {
                        errorMessage = refreshState.error.message.toString()
                    }

                    viewModel.updatePageLiveData(VacancySearchState.Error(errorMessage))
                } else if (isFirstLoading) {
                    viewModel.updatePageLiveData(VacancySearchState.Loading)
                } else if (isListEmpty) {
                    viewModel.updatePageLiveData(VacancySearchState.Empty)
                } else {
                    viewModel.updatePageLiveData(VacancySearchState.Success())
                }
            }
        }
    }

    private fun initFilters() {
        val navController = findNavController()

        val bundle = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<Bundle>("filters_result")
            ?: return

        // если нужно, чтобы не применялось повторно
        navController.currentBackStackEntry?.savedStateHandle?.remove<Bundle>("filters_result")

        viewModel.applyFilters(
            SearchFilters(
                areaCountry = bundle.getInt(KEY_AREA)
                    .takeIf { it != 0 }
                    ?.let { SearchFilters.AreaCountry(it, "") },
                industry = bundle.getInt(KEY_INDUSTRY)
                    .takeIf { it != 0 }
                    ?.let { SearchFilters.Industry(it, "") },
                salary = bundle.getInt(KEY_SALARY).takeIf { it != 0 },
                showSalary = bundle.getBoolean(KEY_ONLY_WITH_SALARY, false)
            )
        )

    }

    /** Обработчик клика при выборе трека */
    private fun selectVacancyHandler(vacancy: VacancyCard) {
        findNavController().navigate(
            R.id.action_vacancySearchFragment_to_vacancyFragment,
            Bundle().apply { putString(VacancyFragment.ARG_VACANCY_ID, vacancy.id) }
        )
    }

    /** Изначальное отображение страницы */
    private fun showNothing() {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.VISIBLE
        _binding?.buttonCount?.visibility = View.GONE

        initPlaceholder(PlaceholderType.NOTHING, "")
    }

    /** Отображение пустой страницы */
    private fun showEmpty() {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.VISIBLE
        _binding?.buttonCount?.visibility = View.GONE

        initPlaceholder(PlaceholderType.EMPTY, getString(R.string.favorites_error_load))
    }

    /** Отображение загрузки */
    private fun showLoading() {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.placeholder?.placeholderInfo?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
    }

    /** Отображение ошибки */
    private fun showError(serverCode: String) {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.VISIBLE
        _binding?.buttonCount?.visibility = View.GONE

        val message = when (serverCode) {
            "-1" -> getString(R.string.no_internet)
            else -> getString(R.string.error)
        }

        initPlaceholder(PlaceholderType.ERROR, message)
    }

    /** Отображение списка вакансий */
    private fun showSuccess(foundVacanciesAmount: Int) {
        _binding?.recyclerList?.visibility = View.VISIBLE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.GONE
        if (foundVacanciesAmount != -1) {
            _binding?.buttonCount?.apply {
                visibility = View.VISIBLE
                text = rootActivity.getString(R.string.vacancies_found_count, foundVacanciesAmount)
            }
        }
    }

    private fun updateFilterIcon() {
        val toolbar = binding.btnBack.menu.findItem(R.id.toolbar_filter)
        toolbar.setIcon(
            if (viewModel.getCurrentFilters() != null) {
                R.drawable.filter_on
            } else {
                R.drawable.filter
            }
        )
    }

    /** Отрисовка placeholder */
    private fun initPlaceholder(type: PlaceholderType, message: String) {
        val imgElement = binding.placeholder.placeholderInfoImg
        val textElement = binding.placeholder.placeholderInfoText
        val imgUrl = when (type) {
            PlaceholderType.NOTHING -> R.drawable.placeholder
            PlaceholderType.ERROR -> R.drawable.placeholder_2
            PlaceholderType.EMPTY -> R.drawable.favorites_error_load
        }

        Glide.with(this)
            .load(imgUrl)
            .into(imgElement)

        textElement.text = message
    }

    /** Рендер состояния страницы */
    private fun renderActivity(state: VacancySearchState) {
        when (state) {
            is VacancySearchState.Nothing -> showNothing()
            is VacancySearchState.Empty -> showEmpty()
            is VacancySearchState.Loading -> showLoading()
            is VacancySearchState.Error -> showError(state.serverCode)
            is VacancySearchState.Success -> showSuccess(state.foundItems)
        }
    }
}
