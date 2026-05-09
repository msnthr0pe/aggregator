package ru.practicum.android.diploma.core.domain.models

data class SearchFilters(
    val areaCountry: AreaCountry? = null,
    val areaRegion: AreaRegion? = null,
    val industry: Industry? = null,
    val salary: Int? = null,
    val showSalary: Boolean? = false,
) {
    data class AreaCountry(
        val id: Int,
        val name: String = "",
    )

    data class AreaRegion(
        val id: Int,
        val name: String = "",
    )

    data class Industry(
        val id: Int,
        val name: String = "",
    )
}
