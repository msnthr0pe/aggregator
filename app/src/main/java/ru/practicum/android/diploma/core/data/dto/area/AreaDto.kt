package ru.practicum.android.diploma.core.data.dto.area

data class AreaDto(
    val id: Int,
    val name: String,
    val parentId: Int,
    val areas: List<AreaDto>
)
