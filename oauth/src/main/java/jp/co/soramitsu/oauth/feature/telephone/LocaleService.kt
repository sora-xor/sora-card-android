package jp.co.soramitsu.oauth.feature.telephone

import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleService @Inject constructor(
    @ApplicationContext private val c: Context,
) {
    private val tm = c.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val code: String = tm.networkCountryIso
}
