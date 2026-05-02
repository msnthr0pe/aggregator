package ru.practicum.android.diploma.favorites.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.core.util.createTitleVacancy
import ru.practicum.android.diploma.core.util.formatSalary
import ru.practicum.android.diploma.core.util.loadSvgInto
import ru.practicum.android.diploma.databinding.VacancyItemBinding

class VacancyAdapter(
    private val onClick: (VacancyDetails) -> Unit
) : RecyclerView.Adapter<VacancyAdapter.VacancyViewHolder>() {
    private val list: MutableList<VacancyDetails> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = VacancyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VacancyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener { onClick(list[position]) }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateVacancies(newVacancies: List<VacancyDetails>) {
        list.clear()
        list.addAll(newVacancies)
        notifyDataSetChanged()
    }

    class VacancyViewHolder(
        private val binding: VacancyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VacancyDetails) {
            binding.vacancyItemTitle.text = createTitleVacancy(model.name, model.address?.city)
            binding.vacancyItemCompany.text = model.employer.name
            binding.vacancyItemSalary.text = formatSalary(model.salary, binding.root.resources)

            loadSvgInto(model.employer.logo, binding.vacancyItemImg)
        }
    }
}
