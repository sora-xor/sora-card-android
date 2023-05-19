package jp.co.soramitsu.oauth.base.sdk

import jp.co.soramitsu.oauth.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryRepo @Inject constructor() {

    var endpointUrl: String = ""
    var username: String = ""
    var password: String = ""
    var soraCardInfo: SoraCardInfo? = null
    var mode: Mode? = null
    var environment: SoraCardEnvironmentType = SoraCardEnvironmentType.NOT_DEFINED
    var client: String = BuildConfig.LIBRARY_PACKAGE_NAME
}

enum class Mode {

    REGISTRATION,
    SIGN_IN
}
