package jp.co.soramitsu.oauth.base.sdk

import javax.inject.Inject
import javax.inject.Singleton
import jp.co.soramitsu.oauth.BuildConfig
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow

@Singleton
class InMemoryRepo @Inject constructor() {

    var locale: String = "en"
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
    var flow: SoraCardFlow? = null

    var soraBackEndUrl = ""
}
