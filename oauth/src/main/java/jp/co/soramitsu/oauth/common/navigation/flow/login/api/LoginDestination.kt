package jp.co.soramitsu.oauth.common.navigation.flow.login.api

sealed interface LoginDestination {

    object TermsAndConditions: LoginDestination

    object EnterPhone: LoginDestination

    @JvmInline
    value class EnterOtp(
        val otpLength: Int
    ): LoginDestination

}