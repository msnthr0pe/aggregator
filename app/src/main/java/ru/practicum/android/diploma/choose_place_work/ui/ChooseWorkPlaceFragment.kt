package ru.practicum.android.diploma.choose_place_work.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseWorkPlaceBinding
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding

class ChooseWorkPlaceFragment : Fragment() {

    private var _binding: FragmentChooseWorkPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseWorkPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseCountryFragment)
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseRegionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
