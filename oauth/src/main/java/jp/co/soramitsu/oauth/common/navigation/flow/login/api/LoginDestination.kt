package jp.co.soramitsu.oauth.common.navigation.flow.login.api

import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations

sealed interface LoginDestination: SoraCardDestinations {

    object TermsAndConditions: LoginDestination {
        override val route: String = "TERMS_AND_CONDITIONS"
    }

    object WebPage: LoginDestination {
        override val route: String = "WebPage"

        const val TITLE_STRING_RES_KEY = "TITLE_STRING_RES_KEY"
        const val URL_KEY = "URL_KEY"
    }

    object EnterPhone: LoginDestination {
        override val route: String = "ENTER_PHONE"
    }

    class EnterOtp(
        val otpLength: Int
    ): LoginDestination {
        override val route: String = "ENTER_OTP"

        companion object: SoraCardDestinations {
            override val route: String = "ENTER_OTP"

            const val OTP_LENGTH_KEY = "OTP_LENGTH"
        }
    }

}