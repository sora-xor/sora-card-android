package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejection

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedScreenState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VerificationRejectedScreenStateTest {

    private lateinit var state: VerificationRejectedScreenState

    @Before
    fun setUp() {
        VerificationRejectedScreenState(
            screenStatus = ScreenStatus.LOADING,
            kycAttemptsCount = 3,
            kycAttemptCostInEuros = 1.0
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT description, image, and telegram support are set up`() {
        Assert.assertEquals(
            Text.StringRes(
                id = R.string.verification_rejected_description
            ),
            state.descriptionText
        )

        Assert.assertEquals(
            R.drawable.ic_verification_rejected,
            state.imageRes
        )

        Assert.assertEquals(
            Text.StringRes(id = R.string.verification_rejected_screen_support_telegram),
            state.telegramSupportText
        )

        Text.StringResWithArgs(
            id = R.string.verification_rejected_screen_attempts_price_disclaimer,
            payload = arrayOf(1.0.toString())
        )
    }

    @Test
    fun `wrong kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycAttemptsCount = 0,
            kycAttemptCostInEuros = 1.0
        )

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.verification_rejected_screen_attempts_used
            ),
            state.kycAttemptsLeftText
        )

        println(state.tryAgainText)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.verification_rejected_screen_try_again_for_euros,
                payload = arrayOf(1.0.toString())
            ),
            state.tryAgainText
        )

        state = state.copy(kycAttemptsCount = -1)

        Assert.assertEquals(
            Text.StringRes(
                id = R.string.verification_rejected_screen_attempts_used
            ),
            state.kycAttemptsLeftText
        )

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.verification_rejected_screen_try_again_for_euros,
                payload = arrayOf(1.0.toDouble().toString())
            ),
            state.tryAgainText
        )
    }

    @Test
    fun `correct kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycAttemptsCount = 3,
            kycAttemptCostInEuros = 1.0
        )

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.verification_rejected_screen_attempts_left,
                payload = arrayOf(3.toString())
            ),
            state.kycAttemptsLeftText
        )

        Assert.assertEquals(
            Text.StringRes(id = R.string.verification_rejected_screen_try_again_for_free),
            state.tryAgainText
        )
    }

    @Test
    fun `test screen state EXPECT elements are marked as shown or hidden`() {
        state = state.copy(screenStatus = ScreenStatus.READY_TO_RENDER)

        Assert.assertEquals(
            true,
            state.shouldKycAttemptsLeftTextBeShown
        )

        Assert.assertEquals(
            true,
            state.shouldKycAttemptsDisclaimerTextBeShown
        )

        Assert.assertEquals(
            true,
            state.shouldTryAgainButtonBeShown
        )

        state = state.copy(screenStatus = ScreenStatus.LOADING)

        Assert.assertEquals(
            false,
            state.shouldKycAttemptsLeftTextBeShown
        )

        Assert.assertEquals(
            false,
            state.shouldKycAttemptsDisclaimerTextBeShown
        )

        Assert.assertEquals(
            false,
            state.shouldTryAgainButtonBeShown
        )

        state = state.copy(screenStatus = ScreenStatus.ERROR)

        Assert.assertEquals(
            false,
            state.shouldKycAttemptsLeftTextBeShown
        )

        Assert.assertEquals(
            false,
            state.shouldKycAttemptsDisclaimerTextBeShown
        )

        Assert.assertEquals(
            false,
            state.shouldTryAgainButtonBeShown
        )
    }
}