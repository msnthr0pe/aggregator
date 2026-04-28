package ru.practicum.android.diploma.vacancysearch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R

class VacancyLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<VacancyLoadStateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_footer, parent, false)
        return ViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class ViewHolder(itemView: View, retry: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.recyclerListLoader)
        private val retryButton = itemView.findViewById<TextView>(R.id.recyclerListRetry)

        init {
            retryButton.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            // Показываем крутилку, если идет загрузка следующей страницы (Append)
            progressBar.isVisible = loadState is LoadState.Loading
            // Показываем кнопку "Повторить", если произошла ошибка
            retryButton.isVisible = loadState is LoadState.Error
        }
    }
}
