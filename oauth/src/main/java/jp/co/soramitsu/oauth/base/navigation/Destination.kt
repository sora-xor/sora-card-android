package jp.co.soramitsu.oauth.base.navigation

enum class Destination(val route: String) {

    INIT_LOADING("oauth/initLoading"),
    TERMS_AND_CONDITIONS("oauth/termsAndConditions"),
    GET_PREPARED("oauth/getPrepared"),
    ENTER_PHONE_NUMBER("oauth/enterPhoneNumber"),
    VERIFY_PHONE_NUMBER("oauth/verifyPhoneNumber"),
    SELECT_COUNTRY("oauth/selectCountry"),
    REGISTER_USER("oauth/registerUser"),
    ENTER_EMAIL("oauth/enterEmail"),
    VERIFY_EMAIL("oauth/verifyEmail"),
    WEB_PAGE("oauth/webPage"),
    CHANGE_EMAIL("oauth/changeEmail"),
    VERIFICATION_FAILED("oauth/verificationFailed"),
    VERIFICATION_REJECTED("oauth/verificationRejected"),
    VERIFICATION_IN_PROGRESS("oauth/verificationInProgress"),
    VERIFICATION_SUCCESSFUL("oauth/verificationSuccessful"),
    CARD_ISSUANCE_OPTIONS("oauth/cardIssuanceOptions"),
    GET_MORE_XOR_DIALOG("oauth/getMoreXorDialog"),
    USER_NOT_FOUND("oauth/userNotFound"),
}

const val COUNTRY_CODE = "country_code"

enum class Argument(val arg: String) {

    EMAIL("email"),
    AUTO_EMAIL_SENT("autoEmailSent"),
    COUNTRY_CODE("countryCode"),
    PHONE_NUMBER("phoneNumber"),
    OTP_LENGTH("otpLength"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    TITLE("title"),
    URL("url"),
    ADDITIONAL_DESCRIPTION("additionalDescription"),
}

fun Argument.path(): String {
    return "/{${this.arg}}"
}

fun Any.asArgument(): String {
    return "/$this"
}
