package ru.practicum.android.diploma.chooseindustry.model

sealed class ChooseIndustryState {
    object Loading : ChooseIndustryState() // Состояние загрузки
    data class Success(val recyclerState: RecyclerState) : ChooseIndustryState() // Список отраслей
    data class Error(val serverCode: String) : ChooseIndustryState() // Ошибка (интернет и т.д.)
}
