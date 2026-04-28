package ru.practicum.android.diploma.core.domain.models

data class VacancyDetails(
    val id: String,
    val name: String,
    val description: String,
    val salary: Salary?,
    val address: Address?,
    val experience: Experience?,
    val schedule: Schedule?,
    val employment: Employment?,
    val contacts: Contacts?,
    val employer: Employer,
    val area: Area,
    val skills: List<String>,
    val url: String,
    val industry: Industry,
) {
    data class Salary(
        val from: Int?,
        val to: Int?,
        val currency: String?
    )

    data class Address(
        val id: String,
        val city: String,
        val street: String,
        val building: String,
        val raw: String
    )

    data class Experience(
        val id: String,
        val name: String
    )

    data class Schedule(
        val id: String,
        val name: String
    )

    data class Employment(
        val id: String,
        val name: String
    )

    data class Contacts(
        val id: String,
        val name: String,
        val email: String,
        val phones: List<Phone>
    )

    data class Phone(
        val comment: String?,
        val formatted: String
    )

    data class Employer(
        val id: String,
        val name: String,
        val logo: String
    )

    data class Area(
        val id: Int,
        val name: String,
        val parentId: Int,
        val areas: List<Area>
    )

    data class Industry(
        val id: Int,
        val name: String
    )
}
