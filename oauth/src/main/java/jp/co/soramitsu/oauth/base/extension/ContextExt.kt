package jp.co.soramitsu.oauth.base.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.isAppAvailableCompat(appName: String): Boolean {
    return try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(appName, 0)
        } else {
            packageManager.getPackageInfo(
                appName,
                PackageManager.PackageInfoFlags.of(0),
            )
        }
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
