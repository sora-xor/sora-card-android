package jp.co.soramitsu.oauth.base.sdk

import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.BuildConfig
import javax.inject.Inject

@ActivityRetainedScoped
class InMemoryRepo @Inject constructor() {

    var endpointUrl: String = ""
    var username: String = ""
    var password: String = ""
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
    var userAvailableXorAmount: Double = 0.toDouble()

    var soraBackEndUrl = ""

    val euroLiquidityThreshold = 100
    val euroCardIssuancePrice = 20

    val kycAttemptPrice: Double = 3.80

    var areAttemptsPaidSuccessfully: Boolean = false
    var isEnoughXorAvailable: Boolean = false
    var isIssuancePaid: Boolean = false
}
