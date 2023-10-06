package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejection

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedScreenState
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
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
            kycFreeAttemptsCount = 3,
            kycAttemptCostInEuros = "1.0",
            isFreeAttemptsLeft = false,
            reason = null,
            reasonDetails = null,
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT description, image, and telegram support are set up`() {
        Text.StringResWithArgs(
            id = R.string.verification_rejected_screen_attempts_price_disclaimer,
            payload = arrayOf(1.0.toString())
        )
    }

    @Test
    fun `wrong kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 0,
            kycAttemptCostInEuros = "1.0"
        )
        assertEquals(R.string.verification_rejected_screen_attempts_used, (state.kycAttemptsLeftText as Text.StringRes).id)
        (state.tryAgainText as Text.StringResWithArgs).let {
            assertEquals(R.string.verification_rejected_screen_try_again_for_euros, it.id)
            assertArrayEquals(arrayOf(1.0.toString()), it.payload)
        }

        state = state.copy(kycFreeAttemptsCount = -1)

        assertEquals(R.string.verification_rejected_screen_attempts_used, (state.kycAttemptsLeftText as Text.StringRes).id)
        (state.tryAgainText as Text.StringResWithArgs).let {
            assertEquals(R.string.verification_rejected_screen_try_again_for_euros, it.id)
            assertArrayEquals(arrayOf(1.0.toString()), it.payload)
        }
    }

    @Test
    fun `correct kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 3,
            kycAttemptCostInEuros = "1.0"
        )
        (state.kycAttemptsLeftText as Text.StringPluralWithArgs).let {
            assertEquals(3, it.amount)
            assertArrayEquals(arrayOf(3), it.payload)
            assertEquals(R.plurals.verification_rejected_screen_attempts_left, it.id)
        }
        assertEquals(R.string.verification_rejected_screen_try_again_for_free, (state.tryAgainText as Text.StringRes).id)
    }
}