package jp.co.soramitsu.oauth.feature.verification.rejected

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.ScreenStatus
import jp.co.soramitsu.oauth.theme.views.Text

data class VerificationRejectedScreenState(
    private val screenStatus: ScreenStatus,
    val kycAttemptsCount: Int,
    val kycAttemptCostInEuros: Double,
    val additionalInfo: String,
    val isFreeAttemptsLeft: Boolean
) {

    val descriptionText: Text =
        Text.StringRes(id = R.string.verification_rejected_description)

    val imageRes: Int =
        R.drawable.ic_verification_rejected

    val shouldKycAttemptsLeftTextBeShown: Boolean = true // Will be available latter

    val kycAttemptsLeftText: Text
        get() {
            if (kycAttemptsCount <= 0)
                return Text.StringRes(id = R.string.verification_rejected_screen_attempts_used)

            return Text.StringResWithArgs(
                id = R.string.verification_rejected_screen_attempts_left,
                payload = arrayOf(kycAttemptsCount.toString())
            )
        }

    val shouldKycAttemptsDisclaimerTextBeShown: Boolean = false // Will be available latter

    val kycAttemptsDisclaimerText: Text =
        Text.StringResWithArgs(
            id = R.string.verification_rejected_screen_attempts_price_disclaimer,
            payload = arrayOf(kycAttemptCostInEuros.toString())
        )

    val shouldTryAgainButtonBeShown: Boolean = isFreeAttemptsLeft

    val tryAgainText: Text
        get() {
            /* Will be available latter */
//            if (kycAttemptsCount <= 0)
//                return Text.StringResWithArgs(
//                    id = R.string.verification_rejected_screen_try_again_for_euros,
//                    payload = arrayOf(kycAttemptCostInEuros.toString())
//                )

            return Text.StringRes(id = R.string.verification_rejected_screen_try_again_for_free)
        }

    val telegramSupportText: Text =
        Text.StringRes(id = R.string.verification_rejected_screen_support_telegram)

}