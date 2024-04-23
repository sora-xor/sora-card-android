package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus

data class VerificationRejectedScreenState(
    private val screenStatus: ScreenStatus,
    val kycFreeAttemptsCount: Int,
    val isFreeAttemptsLeft: Boolean,
    val kycAttemptCostInEuros: String,
    val reason: String?,
    val reasonDetails: List<String>?,
    val phone: String,
) {

    val kycAttemptsLeftText: TextValue
        get() {
            if (kycFreeAttemptsCount <= 0) {
                return TextValue.StringRes(id = R.string.verification_rejected_screen_attempts_used)
            }

            return TextValue.StringPluralWithArgs(
                id = R.plurals.verification_rejected_screen_attempts_left,
                amount = kycFreeAttemptsCount,
                payload = arrayOf(kycFreeAttemptsCount),
            )
        }

    val shouldTryAgainButtonBeEnabled: Boolean = kycFreeAttemptsCount > 0

    val tryAgainText: TextValue
        get() {
            if (kycFreeAttemptsCount <= 0) {
                return TextValue.StringResWithArgs(
                    id = R.string.verification_rejected_screen_try_again_for_euros,
                    payload = arrayOf(kycAttemptCostInEuros),
                )
            }

            return TextValue.StringRes(
                id = R.string.verification_rejected_screen_try_again_for_free,
            )
        }
}
