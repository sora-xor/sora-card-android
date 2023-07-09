package jp.co.soramitsu.oauth.common.navigation.flow.registration.api

import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations

sealed interface RegistrationDestination: SoraCardDestinations {

    object EnterFirstAndLastName: RegistrationDestination {
        override val route: String = "ENTER_FIRST_AND_LAST_NAME"
    }

    class EnterEmail(
        val firstName: String,
        val lastName: String
    ): RegistrationDestination {
        override val route: String = "ENTER_EMAIL"

        companion object: SoraCardDestinations {
            override val route: String = "ENTER_EMAIL"

            const val FIRST_NAME_KEY = "FIRST_NAME_KEY"
            const val LAST_NAME_KEY = "LAST_NAME_KEY"
        }
    }

    class EmailConfirmation(
        val email: String,
        val autoEmailBeenSent: Boolean
    ): RegistrationDestination {
        override val route: String = "SEND_VERIFICATION_EMAIL"

        companion object: SoraCardDestinations {
            override val route: String = "SEND_VERIFICATION_EMAIL"

            const val EMAIL_KEY = "EMAIL_KEY"
            const val AUTO_EMAIL_BEEN_SENT_KEY = "AUTO_EMAIL_BEEN_SENT_KEY"
        }
    }

}