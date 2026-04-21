package ru.practicum.android.diploma.core.data.dto

/**
 * Формат ответа от сервера
 *
 * Так как с сервера может приходить и объект, и простой массив, то
 * данные от сервера помещаем в result
 * */
open class Response<out T> (resultProp: T? = null) {
    var resultCode = 0
    val result = resultProp
}
