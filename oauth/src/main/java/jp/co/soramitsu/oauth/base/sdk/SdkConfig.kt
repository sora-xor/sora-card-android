package jp.co.soramitsu.oauth.base.sdk

import jp.co.soramitsu.oauth.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryRepo @Inject constructor() {

    var endpointUrl: String = ""
    var username: String = ""
    var password: String = ""
    var mode: Mode? = null
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
    var userAvailableXorAmount: Double = 0.toDouble()

    val euroLiquidityThreshold = 100
    val euroCardIssuancePrice = 20

    val kycAttemptPrice: Double = 3.80

    var areAttemptsPaidSuccessfully: Boolean = false
    var isEnoughXorAvailable: Boolean = false
    var isIssuancePaid: Boolean = false
}

enum class Mode {

    REGISTRATION,
    SIGN_IN
}
