package jp.co.soramitsu.oauth.base.sdk

import javax.inject.Inject
import javax.inject.Singleton
import jp.co.soramitsu.oauth.BuildConfig

@Singleton
class InMemoryRepo @Inject constructor() {

    var locale: String = "en"
    var endpointUrl: String = ""
    var username: String = ""
    var password: String = ""
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
    var userAvailableXorAmount: Double = 0.toDouble()

    var soraBackEndUrl = ""

    val euroLiquidityThreshold = 100

    var areAttemptsPaidSuccessfully: Boolean = false
    var isEnoughXorAvailable: Boolean = false
    var isIssuancePaid: Boolean = false
}