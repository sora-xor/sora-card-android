package jp.co.soramitsu.oauth.common.navigation.router.api

enum class SoraCardDestinations(val route: String) {

    /* Helper */
    Loading("LOADING"),

    /* Login Flow */
    TermsAndConditions("TERMS_AND_CONDITIONS"),
    EnterPhone("ENTER_PHONE"),
    EnterOtp("ENTER_OTP"),

    /* Registration Flow */
    EnterFirstAndLastName("ENTER_FIRST_AND_LAST_NAME"),
    EnterEmail("ENTER_EMAIL"),
    SendVerificationEmail("SEND_VERIFICATION_EMAIL"),

    /* Verification Flow */
    NotEnoughXor("NOT_ENOUGH_XOR"),
    GetPrepared("GET_PREPARED"),
    VerificationSuccessful("VERIFICATION_IN_SUCCESSFUL"),
    VerificationInProgress("VERIFICATION_IN_PROGRESS"),
    VerificationRejected("VERIFICATION_REJECTED"),
    GetMoreXor("GET_MORE_XOR")

}