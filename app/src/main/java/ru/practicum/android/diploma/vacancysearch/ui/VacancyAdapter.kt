package ru.practicum.android.diploma.vacancysearch.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.core.domain.models.VacancyCard
import ru.practicum.android.diploma.core.domain.models.VacancyCardSalary

class VacancyAdapter(
    private val onClick: (VacancyCard) -> Unit
): PagingDataAdapter<VacancyCard, VacancyAdapter.VacancyViewHolder>(UserDiffCallback) {
    var list = ArrayList<VacancyCard>()

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
//        holder.itemView.setOnClickListener { onClick(list[position]) }
//        holder.bind(list[position])

        val item = getItem(position)
        if (item != null) {
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


            //        val roundedVal: Float = itemView.context.resources.getDimension(R.dimen.track_image_border_px)

            Log.i("TEST", model.logo.toString())


            Glide.with(itemView)
                .load(model.logo)
                .placeholder(R.drawable.vacancy_placeholder)
                //                .transform(RoundedCorners(Converter.dpToPx(roundedVal, itemView.context)))
                .into(img)
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


// class VacancyAdapter(
//    private val onClick: (VacancyCard) -> Unit
//): RecyclerView.Adapter<VacancyAdapter.VacancyViewHolder>() {
//    var list = ArrayList<VacancyCard>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.vacancy_item, parent, false)
//        return VacancyViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
//        holder.itemView.setOnClickListener { onClick(list[position]) }
//        holder.bind(list[position])
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun setList(newList: List<VacancyCard>) {
//        list.clear()
//        list.addAll(newList)
//        notifyDataSetChanged()
//    }
//
//    class VacancyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        private val img = itemView.findViewById<ImageView>(R.id.vacancyItemImg)
//        private val title = itemView.findViewById<TextView>(R.id.vacancyItemTitle)
//        private val company = itemView.findViewById<TextView>(R.id.vacancyItemCompany)
//        private val salary = itemView.findViewById<TextView>(R.id.vacancyItemSalary)
//
//        fun bind(model: VacancyCard) {
//            title.text = createTitle(model.name, model.city)
//            company.text = model.company ?: ""
//            salary.text = createSalary(model.salary)
//
//
//    //        val roundedVal: Float = itemView.context.resources.getDimension(R.dimen.track_image_border_px)
//
//            Log.i("TEST", model.logo.toString())
//
//
//            Glide.with(itemView)
//                .load(model.logo)
//                .placeholder(R.drawable.vacancy_placeholder)
////                .transform(RoundedCorners(Converter.dpToPx(roundedVal, itemView.context)))
//                .into(img)
//        }
//
//        private fun createTitle(name: String, city: String?): String {
//            var title = name
//
//            if (city != null) {
//                title += ", $city"
//            }
//
//            return title
//        }
//
//        private fun createSalary(salary: VacancyCardSalary): String {
//            var res = ""
//
//            if (salary.from != null) {
//                res += "от ${salary.from}"
//            }
//
//            if (salary.to != null) {
//                res += "до ${salary.to}"
//            }
//
//            if (salary.from != null && salary.to != null && salary.currency != null) {
//                res += "${salary.currency}"
//            }
//
//            // from to currency
//
//            return res
//        }
//    }
//}
