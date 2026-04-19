package ru.practicum.android.diploma.core.data.network

import ru.practicum.android.diploma.core.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}
