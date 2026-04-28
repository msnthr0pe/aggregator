package ru.practicum.android.diploma.vacancysearch.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.databinding.FragmentVacancySearchBinding
import ru.practicum.android.diploma.vacancysearch.ui.state.PlaceholderType
import ru.practicum.android.diploma.vacancysearch.ui.state.VacancySearchState

class VacancySearchFragment : Fragment() {

    private val viewModel by viewModel<VacancySearchViewModel>()
    private var _binding: FragmentVacancySearchBinding? = null
    private val binding get() = _binding!!
    private var vacancyAdapter = VacancyAdapter { selectVacancyHandler(it) }

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

        binding.recyclerList.adapter = vacancyAdapter

        lifecycleScope.launch {
            viewModel.items.collectLatest { pagingData ->
                vacancyAdapter.submitData(pagingData)
            }
        }

        viewModel.observePage().observe(viewLifecycleOwner) {
            renderActivity(it)
        }

        initSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Инициализация поисковика */
    private fun initSearch() {
        _binding?.search?.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchQueryChanged(text.toString().trim())
        }

        _binding?.searchWrapper?.setEndIconOnClickListener {
            _binding?.search?.text?.clear()
        }
    }

    /** Обработчик клика при выборе трека */
    private fun selectVacancyHandler(vacancy: VacancyCard) {
        Log.i("TEST", vacancy.toString())
//        if (clickDebounce()) {
//            findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
//                bundleOf(PlayerFragment.EXTRA_TRACK_KEY to track))
//        }
    }

    /** Изначальное отображение страницы */
    private fun showNothing() {
        _binding?.recyclerList?.visibility = View.VISIBLE // TODO: ПОМЕНЯТЬ
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.GONE

        initPlaceholder(PlaceholderType.NOTHING, "")
    }

    /** Отображение пустой страницы */
    private fun showEmpty() {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.VISIBLE

        initPlaceholder(PlaceholderType.EMPTY, getString(R.string.favorites_error_load))
    }

    /** Отображение загрузки */
    private fun showLoading() {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.placeholder?.placeholderInfo?.visibility = View.GONE
    }

    /** Отображение ошибки */
    private fun showError(serverCode: String) {
        _binding?.recyclerList?.visibility = View.GONE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.VISIBLE

        val message = when(serverCode) {
            "-1" -> getString(R.string.no_internet)
            else -> getString(R.string.error)
        }

        initPlaceholder(PlaceholderType.ERROR, message)
    }

    /** Отображение списка вакансий */
    private fun showSuccess(list: List<VacancyCard>) {
        _binding?.recyclerList?.visibility = View.VISIBLE
        _binding?.buttonCount?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.placeholder?.placeholderInfo?.visibility = View.GONE

//        vacancyAdapter.setList(list)
    }

    /** Отрисовка placeholder */
    private fun initPlaceholder(type: PlaceholderType, message: String) {
        val imgElement = _binding?.placeholder?.placeholderInfoImg
        val textElement = _binding?.placeholder?.placeholderInfoText
        val imgUrl = when(type) {
            PlaceholderType.NOTHING -> R.drawable.placeholder
            PlaceholderType.ERROR -> R.drawable.placeholder_2
            PlaceholderType.EMPTY -> R.drawable.favorites_error_load
        }

        if (imgElement != null) {
            Glide.with(this)
                .load(imgUrl)
                .into(imgElement)
        }

        if (textElement != null) {
            textElement.text = message
        }
    }

    /** Рендер состояния страницы */
    private fun renderActivity(state: VacancySearchState) {
        when(state) {
            is VacancySearchState.Nothing -> showNothing()
            is VacancySearchState.Empty -> showEmpty()
            is VacancySearchState.Loading -> showLoading()
            is VacancySearchState.Error -> showError(state.serverCode)
            is VacancySearchState.Success -> showSuccess(state.data)
        }
    }
}
