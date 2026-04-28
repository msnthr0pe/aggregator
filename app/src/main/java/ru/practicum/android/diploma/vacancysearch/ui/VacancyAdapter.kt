package ru.practicum.android.diploma.vacancysearch.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.core.util.createTitleVacancy
import ru.practicum.android.diploma.core.util.formatSalary
import ru.practicum.android.diploma.core.util.loadSvgInto
import ru.practicum.android.diploma.databinding.VacancyItemBinding

class VacancyAdapter(
    private val onClick: (VacancyCard) -> Unit
) : PagingDataAdapter<VacancyCard, VacancyAdapter.VacancyViewHolder>(UserDiffCallback) {
    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.itemView.setOnClickListener { onClick(item) }
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = VacancyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return VacancyViewHolder(binding)
    }

    object UserDiffCallback : DiffUtil.ItemCallback<VacancyCard>() {
        override fun areItemsTheSame(oldItem: VacancyCard, newItem: VacancyCard) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VacancyCard, newItem: VacancyCard) = oldItem == newItem
    }

    class VacancyViewHolder(
        private val binding: VacancyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: VacancyCard) {
            binding.vacancyItemTitle.text = createTitleVacancy(model.name, model.city)
            binding.vacancyItemCompany.text = model.company ?: ""
            binding.vacancyItemSalary.text = formatSalary(model.salary, binding.root.resources)

            loadSvgInto(model.logo ?: "", binding.vacancyItemImg)
        }
    }
}
