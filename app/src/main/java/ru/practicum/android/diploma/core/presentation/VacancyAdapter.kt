package ru.practicum.android.diploma.core.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.databinding.VacancyItemBinding

class VacancyAdapter(
    private val onVacancyClick: (VacancyDetails) -> Unit
) : RecyclerView.Adapter<VacancyViewHolder>() {

    private val vacancies: MutableList<VacancyDetails> = mutableListOf()

    fun updateVacancies(newVacancies: List<VacancyDetails>) {
        vacancies.clear()
        vacancies.addAll(newVacancies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = VacancyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VacancyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(vacancies[position])

        holder.itemView.setOnClickListener {
            onVacancyClick(vacancies[position])
        }
    }

    override fun getItemCount(): Int = vacancies.size

}
