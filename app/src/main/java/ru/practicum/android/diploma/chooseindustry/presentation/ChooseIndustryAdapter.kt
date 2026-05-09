package ru.practicum.android.diploma.chooseindustry.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.core.domain.models.VacancyDetails
import ru.practicum.android.diploma.databinding.IndustryItemBinding

class ChooseIndustryAdapter(
    private val onClick: (VacancyDetails.Industry) -> Unit
) : RecyclerView.Adapter<ChooseIndustryAdapter.ChooseIndustryViewHolder>() {
    private val list: MutableList<VacancyDetails.Industry> = mutableListOf()
    private var selectedId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseIndustryViewHolder {
        val binding = IndustryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChooseIndustryViewHolder(binding)
    }

    /**
     * Установка дефолтного значения
     *
     * notifyDataSetChanged не нужен, так как запрос происходит позже
     */
    fun setDefaultId(id: Int) {
        selectedId = id
    }

    override fun onBindViewHolder(holder: ChooseIndustryViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            selectedId = list[position].id
            onClick(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(industries: List<VacancyDetails.Industry>) {
        list.clear()
        list.addAll(industries)
        notifyDataSetChanged()
    }

    inner class ChooseIndustryViewHolder(
        private val binding: IndustryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: VacancyDetails.Industry) {
            binding.industryItemName.text = model.name
            binding.industryItemButton.isChecked = model.id == selectedId
        }
    }
}
