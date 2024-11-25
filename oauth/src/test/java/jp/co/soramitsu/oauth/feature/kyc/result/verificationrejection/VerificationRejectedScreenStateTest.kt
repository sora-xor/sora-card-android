package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejection

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected.VerificationRejectedScreenState
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus
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
            phone = "+987",
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT description, image, and telegram support are set up`() {
        TextValue.StringResWithArgs(
            id = R.string.verification_rejected_screen_attempts_price_disclaimer,
            payload = arrayOf(1.0.toString()),
        )
    }

    @Test
    fun `wrong kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 0,
            kycAttemptCostInEuros = "1.0",
        )
        assertEquals(
            R.string.verification_rejected_screen_attempts_used,
            (state.kycAttemptsLeftText as TextValue.StringRes).id,
        )
        (state.tryAgainText as TextValue.StringResWithArgs).let {
            assertEquals(R.string.verification_rejected_screen_try_again_for_euros, it.id)
            assertArrayEquals(arrayOf(1.0.toString()), it.payload)
        }

        state = state.copy(kycFreeAttemptsCount = -1)

        assertEquals(
            R.string.verification_rejected_screen_attempts_used,
            (state.kycAttemptsLeftText as TextValue.StringRes).id,
        )
        (state.tryAgainText as TextValue.StringResWithArgs).let {
            assertEquals(R.string.verification_rejected_screen_try_again_for_euros, it.id)
            assertArrayEquals(arrayOf(1.0.toString()), it.payload)
        }
    }

    @Test
    fun `correct kyc attempt set EXPECT texts are set`() {
        state = state.copy(
            screenStatus = ScreenStatus.READY_TO_RENDER,
            kycFreeAttemptsCount = 3,
            kycAttemptCostInEuros = "1.0",
        )
        (state.kycAttemptsLeftText as TextValue.StringPluralWithArgs).let {
            assertEquals(3, it.amount)
            assertArrayEquals(arrayOf(3), it.payload)
            assertEquals(R.plurals.verification_rejected_screen_attempts_left, it.id)
        }
        assertEquals(
            R.string.verification_rejected_screen_try_again_for_free,
            (state.tryAgainText as TextValue.StringRes).id,
        )
    }
}
