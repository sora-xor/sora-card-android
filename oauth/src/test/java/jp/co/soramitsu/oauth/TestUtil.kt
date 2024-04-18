package jp.co.soramitsu.oauth

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

fun Any.getFileContentFromResources(fileName: String): String {
    return getResourceReader(fileName).readText()
}

fun Any.getResourceReader(fileName: String): Reader {
    val stream = javaClass.classLoader!!.getResourceAsStream(fileName)

    return BufferedReader(InputStreamReader(stream))
}

fun <T> anyNonNull(): T {
    Mockito.any<T>()
    return initialized()
}

fun <T : Any> eqNonNull(value: T): T = eq(value) ?: value

private fun <T> initialized(): T = null as T

/**
 * [https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04]
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

suspend fun <T> LiveData<T>.observeForTesting(
    checker: (Int, T) -> Unit,
    block: suspend () -> Unit,
) {
    var i = 0
    val observer = Observer<T> {
        checker(i++, it)
    }
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}
