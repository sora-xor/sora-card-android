package jp.co.soramitsu.oauth.feature.registration.sendverificationemail

import jp.co.soramitsu.oauth.theme.views.ButtonState

data class VerifyEmailState(
    val resendLinkButtonState: ButtonState = ButtonState(title = "", enabled = false),
    val changeEmailButtonState: ButtonState = ButtonState(title = "", enabled = true),
    val email: String = "",
    val autoSentEmail: Boolean = false
)
