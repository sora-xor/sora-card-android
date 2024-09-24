package jp.co.soramitsu.oauth.base.sdk

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton
import jp.co.soramitsu.oauth.BuildConfig
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.network.NetworkRequest

@Singleton
class InMemoryRepo @Inject constructor() {

    var locale: String = "en"
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
    var flow: SoraCardFlow? = null

    var soraBackEndUrl = ""
    var ghExpectedExchangeVolume: Int? = null
    var ghEmploymentStatus: Int? = null
    var ghExchangeReason = emptyList<Int>()
    var ghSourceOfFunds = emptyList<Int>()
    var ghCountriesFrom = emptyList<String>()
    var ghCountriesTo = emptyList<String>()

    val networkHeader =
        "$client/${Build.MANUFACTURER}/${Build.MODEL}/${Build.VERSION.SDK_INT}"

    fun url(baseUrl: String?, request: NetworkRequest): String =
        "${baseUrl ?: soraBackEndUrl}${request.url}"
}
