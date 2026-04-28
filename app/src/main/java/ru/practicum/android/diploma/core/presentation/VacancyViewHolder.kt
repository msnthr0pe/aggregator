package ru.practicum.android.diploma.core.presentation

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.util.formatSalary
import ru.practicum.android.diploma.core.util.loadPicInto
import ru.practicum.android.diploma.databinding.VacancyItemBinding

class VacancyViewHolder(
    private val binding: VacancyItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: VacancyDetails) {
        binding.vacancyItemTitle.text = vacancy.name
        binding.vacancyItemCompany.text = vacancy.employer.name
        binding.vacancyItemSalary.text = vacancy.formatSalary(binding.root.resources)

        if (vacancy.employer.logo.isNotEmpty()) {
            loadPicInto(binding.root.context, vacancy.employer.logo, binding.vacancyItemImg)
        } else {
            binding.vacancyItemImg.setImageResource(R.drawable.vacancy_placeholder)
        }

    }

}
