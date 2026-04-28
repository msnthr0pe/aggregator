package ru.practicum.android.diploma.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.presentation.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.favorites.presentation.FavoritesState
import ru.practicum.android.diploma.favorites.presentation.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private var adapter: VacancyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        adapter = VacancyAdapter { vacancy ->
            val gson = Gson()
            val vacancyJson = gson.toJson(vacancy)

            findNavController().navigate(
                R.id.action_favoritesFragment_to_vacancyFragment,
                Bundle().apply { putString("vacancy_json", vacancyJson) }
            )
        }
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> showEmpty()
                is FavoritesState.Error -> showError()
                is FavoritesState.Content -> showContent(state.vacancies)
            }
        }
    }

    private fun showEmpty() {
        binding.favoritesRecyclerView.isVisible = false
        binding.placeholderEmptyList.isVisible = true
        binding.placeholderErrorLoadList.isVisible = false
    }

    private fun showError() {
        binding.favoritesRecyclerView.isVisible = false
        binding.placeholderEmptyList.isVisible = false
        binding.placeholderErrorLoadList.isVisible = true
    }

    private fun showContent(vacancies: List<VacancyDetails>) {
        binding.favoritesRecyclerView.isVisible = true
        binding.placeholderEmptyList.isVisible = false
        binding.placeholderErrorLoadList.isVisible = false

        adapter?.updateVacancies(vacancies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
