package jp.co.soramitsu.oauth.core.engines.router.api

sealed class SoraCardDestinations(val route: String) {

    /* Helper */
    object Loading: SoraCardDestinations("LOADING")

    /* Login Flow */
    object TermsAndConditions: SoraCardDestinations("TERMS_AND_CONDITIONS")
    object EnterPhone: SoraCardDestinations("ENTER_PHONE")
    class EnterOtp(
        otpLength: Int
    ): SoraCardDestinations("ENTER_OTP/{$otpLength}") {
        companion object {
            const val template = "ENTER_OTP/{otpLength}"
        }
    }

    /* Registration Flow */

    object EnterFirstAndLastName: SoraCardDestinations("ENTER_FIRST_AND_LAST_NAME")
    class EnterEmail(
        firstName: String,
        lastName: String
    ): SoraCardDestinations("ENTER_EMAIL/{$firstName}/{$lastName}") {
        companion object {
            const val template = "ENTER_EMAIL/{firstName}/{lastName}"
        }
    }
    class SendVerificationEmail(
        email: String,
        autoEmailBeenSent: Boolean
    ): SoraCardDestinations("SEND_VERIFICATION_EMAIL/{$email}/{$autoEmailBeenSent}") {
        companion object {
            const val template = "SEND_VERIFICATION_EMAIL/{email}/{autoEmailBeenSent}"
        }
    }

    /* Verification Flow */
    object NotEnoughXor: SoraCardDestinations("NOT_ENOUGH_XOR")
    object GetPrepared: SoraCardDestinations("GET_PREPARED")
    object VerificationSuccessful: SoraCardDestinations("VERIFICATION_IN_SUCCESSFUL")
    object VerificationInProgress: SoraCardDestinations("VERIFICATION_IN_PROGRESS")
    class VerificationRejected(
        additionalInfo: String?
    ): SoraCardDestinations("VERIFICATION_REJECTED/{$additionalInfo}") {
        companion object {
            const val template = "VERIFICATION_REJECTED/{additionalInfo}"
        }
    }
    class VerificationFailed(
        additionalInfo: String?
    ): SoraCardDestinations("VERIFICATION_FAILED/{$additionalInfo}") {
        companion object {
            const val template = "VERIFICATION_FAILED/{additionalInfo}"
        }
    }
    object GetMoreXor: SoraCardDestinations("GET_MORE_XOR")
}