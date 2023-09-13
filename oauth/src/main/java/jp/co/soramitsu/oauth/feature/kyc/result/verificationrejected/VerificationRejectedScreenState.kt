package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text

data class VerificationRejectedScreenState(
    private val screenStatus: ScreenStatus,
    val kycFreeAttemptsCount: Int,
    val isFreeAttemptsLeft: Boolean,
    val kycAttemptCostInEuros: Double,
) {

    val kycAttemptsLeftText: Text
        get() {
            if (kycFreeAttemptsCount <= 0)
                return Text.StringRes(id = R.string.verification_rejected_screen_attempts_used)

            return Text.StringPluralWithArgs(
                id = R.plurals.verification_rejected_screen_attempts_left,
                amount = kycFreeAttemptsCount,
                payload = arrayOf(kycFreeAttemptsCount),
            )
        }

    val shouldTryAgainButtonBeEnabled: Boolean = kycFreeAttemptsCount > 0

    val tryAgainText: Text
        get() {
            if (kycFreeAttemptsCount <= 0)
                return Text.StringResWithArgs(
                    id = R.string.verification_rejected_screen_try_again_for_euros,
                    payload = arrayOf(kycAttemptCostInEuros.toString())
                )

            return Text.StringRes(id = R.string.verification_rejected_screen_try_again_for_free)
        }
}
