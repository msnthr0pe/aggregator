package ru.practicum.android.diploma.vacancysearch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.core.domain.models.VacancyCardSalary

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vacancy_item, parent, false)
        return VacancyViewHolder(view)
    }

    object UserDiffCallback : DiffUtil.ItemCallback<VacancyCard>() {
        override fun areItemsTheSame(oldItem: VacancyCard, newItem: VacancyCard) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VacancyCard, newItem: VacancyCard) = oldItem == newItem
    }
    class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img = itemView.findViewById<ImageView>(R.id.vacancyItemImg)
        private val title = itemView.findViewById<TextView>(R.id.vacancyItemTitle)
        private val company = itemView.findViewById<TextView>(R.id.vacancyItemCompany)
        private val salary = itemView.findViewById<TextView>(R.id.vacancyItemSalary)

        fun bind(model: VacancyCard) {
            title.text = createTitle(model.name, model.city)
            company.text = model.company ?: ""
            salary.text = createSalary(model.salary)

//            Glide.with(itemView)
//                .load(model.logo)
//                .placeholder(R.drawable.vacancy_placeholder)
//                //                .transform(RoundedCorners(Converter.dpToPx(roundedVal, itemView.context)))
//                .into(img)
        }

        private fun createTitle(name: String, city: String?): String {
            var title = name

            if (city != null) {
                title += ", $city"
            }

            return title
        }

        private fun createSalary(salary: VacancyCardSalary): String {
            var res = ""

            if (salary.from != null) {
                res += "от ${salary.from}"
            }

            if (salary.to != null) {
                res += "до ${salary.to}"
            }

            if (salary.from != null && salary.to != null && salary.currency != null) {
                res += "${salary.currency}"
            }

            // from to currency

            return res
        }
    }
}
