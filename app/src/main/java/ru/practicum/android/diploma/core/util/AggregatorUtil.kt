package ru.practicum.android.diploma.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ImageView
import co.touchlab.stately.concurrency.AtomicBoolean
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val DEFAULT_DEBOUNCE_DELAY = 300L

fun Context.hasNetwork(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    if (network == null || capabilities == null) {
        return false
    }
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}

fun <T> debounce(
    waitMs: Long = DEFAULT_DEBOUNCE_DELAY,
    scope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun clickDebounce(
    waitMs: Long = DEFAULT_DEBOUNCE_DELAY,
    scope: CoroutineScope,
    isClickAllowed: AtomicBoolean
): Boolean {
    val current = isClickAllowed
    if (isClickAllowed.value) {
        isClickAllowed.value = false
        scope.launch {
            delay(waitMs)
            isClickAllowed.value = true
        }
    }
    return current.value
}

fun loadPicInto(context: Context, url: String, image: ImageView) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .into(image)
}

