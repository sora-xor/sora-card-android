package jp.co.soramitsu.oauth.common.navigation.flow.verification.api

import jp.co.soramitsu.oauth.core.engines.router.api.SoraCardDestinations


sealed interface VerificationDestination: SoraCardDestinations {

    object GetPrepared: VerificationDestination {
        override val route: String = "GET_PREPARED"
    }

    object NotEnoughXor: VerificationDestination {
        override val route: String = "NOT_ENOUGH_XOR"
    }

    object VerificationInProgress: VerificationDestination {
        override val route: String = "VERIFICATION_IN_PROGRESS"
    }

    object VerificationSuccessful: VerificationDestination {
        override val route: String = "VERIFICATION_SUCCESSFUL"
    }

    class VerificationRejected(
        val additionalInfo: String?
    ): VerificationDestination {
        override val route: String = "VERIFICATION_REJECTED"

        companion object: SoraCardDestinations {
            override val route: String = "VERIFICATION_FAILED"

            const val ADDITIONAL_INFO_KEY = "ADDITIONAL_INFO_KEY"
        }
    }

    class VerificationFailed(
        val additionalInfo: String?
    ): VerificationDestination {
        override val route: String = "VERIFICATION_FAILED"

        companion object: SoraCardDestinations {
            override val route: String = "VERIFICATION_FAILED"

            const val ADDITIONAL_INFO_KEY = "ADDITIONAL_INFO_KEY"
        }
    }

    object GetMoreXor: VerificationDestination {
        override val route: String = "GET_MORE_XOR"
    }

}