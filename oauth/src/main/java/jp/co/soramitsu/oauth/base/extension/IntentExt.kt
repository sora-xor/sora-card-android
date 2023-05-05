package jp.co.soramitsu.oauth.base.extension

import android.content.Intent
import android.os.Build
import android.os.Bundle

fun <T> Intent.getParcelableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        this.setExtrasClassLoader(clazz.classLoader)
        extras?.getParcelable(key)
    } else {
        getParcelableExtra(key, clazz)
    }
}

fun <T> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        this.classLoader = clazz.classLoader
        getParcelable(key)
    } else {
        getParcelable(key, clazz)
    }
}
