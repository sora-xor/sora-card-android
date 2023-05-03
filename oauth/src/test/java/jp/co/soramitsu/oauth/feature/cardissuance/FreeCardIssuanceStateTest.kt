package jp.co.soramitsu.oauth.feature.cardissuance

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text
import jp.co.soramitsu.oauth.feature.cardissuance.state.FreeCardIssuanceState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FreeCardIssuanceStateTest {

    private lateinit var state: FreeCardIssuanceState

    @Before
    fun setUp() {
        FreeCardIssuanceState(
            xorCurrentAmount = 0f,
            xorInsufficientAmount = 0f
        ).apply { state = this }
    }

    @Test
    fun `init EXPECT title and description set up`() {
        Assert.assertEquals(
            Text.StringRes(R.string.card_issuance_screen_free_card_title),
            state.titleText
        )

        Assert.assertEquals(
            Text.StringRes(R.string.card_issuance_screen_free_card_description),
            state.descriptionText
        )
    }

    /**
     * User loans xor to us, but:
     *  1) we also loan to him xor(for some reason? possible bug on backend) -> balance indicator is null
     *  2) he needs 0 xor(for some reason? possible bug on backend) -> balance indicator is null
     *  3) he has to buy more xor -> show balance indicator
     */
    @Test
    fun `xorCurrentAmount = -1, xorInsufficientAmount = x EXPECT balance indicator is correctly set up`() {
        state = state.copy(xorCurrentAmount = -1f, xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.details_need_xor_desription,
                payload = arrayOf(2f.toString(), (-1f).toString())
            ),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0.5f, state.xorSufficiencyPercentage)
    }

    /**
     * User has no xor on account, but:
     *  1) we loan him xor(for some reason? possible bug on backend) -> do not show get xor button
     *  2) he needs 0 xor(for some reason? possible bug on backend) -> do not show get xor button
     *  3) he has to buy more xor -> show balance indicator
     */
    @Test
    fun `xorCurrentAmount = 0, xorInsufficientAmount = x EXPECT balance indicator is correctly set up`() {
        state = state.copy(xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.details_need_xor_desription,
                payload = arrayOf(1f.toString(), 0f.toString())
            ),
            state.xorSufficiencyText
        )

        Assert.assertEquals(1f, state.xorSufficiencyPercentage)
    }

    /**
     * User has xor on account, but:
     *  1) we loan him xor(for some reason? possible bug on backend) -> do not show get xor button
     *  2) he needs 0 xor(for some reason? 0.00000001 for example) -> show balance indicator
     *  3) he has to buy more xor -> show balance indicator
     */
    @Test
    fun `xorCurrentAmount = 1, xorInsufficientAmount = x EXPECT balance indicator is correctly set up`() {
        state = state.copy(xorCurrentAmount = 1f, xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.cant_fetch_data),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.details_need_xor_desription,
                payload = arrayOf(0f.toString(), 1f.toString())
            ),
            state.xorSufficiencyText
        )

        Assert.assertEquals(1f, state.xorSufficiencyPercentage)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.details_need_xor_desription,
                payload = arrayOf(1f.toString(), 1f.toString())
            ),
            state.xorSufficiencyText
        )

        Assert.assertEquals(0.5f, state.xorSufficiencyPercentage)
    }

    /**
     * User loans xor to us, but:
     *  1) we also loan to him xor(for some reason? possible bug on backend) -> do not show get xor button
     *  2) he needs 0 xor(for some reason? possible bug on backend) -> do not show get xor button
     *  3) he has to buy more xor -> show get xor button
     */
    @Test
    fun `xorCurrentAmount = -1, xorInsufficientAmount = x EXPECT get xor is correctly set up`() {
        state = state.copy(xorCurrentAmount = -1f, xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor),
            state.getInsufficientXorText
        )

        Assert.assertEquals(false, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor),
            state.getInsufficientXorText
        )

        Assert.assertEquals(false, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_get_xor,
                payload = arrayOf(2f.toString())
            ),
            state.getInsufficientXorText
        )

        Assert.assertEquals(true, state.shouldGetInsufficientXorButtonBeShown)
    }

    /**
     * User has no xor on account, but:
     *  1) we loan him xor(for some reason? possible bug on backend) -> do not show get xor button
     *  2) he needs 0 xor(for some reason? possible bug on backend) -> do not show get xor button
     *  3) he has to buy more xor -> show get xor button
     */
    @Test
    fun `xorCurrentAmount = 0, xorInsufficientAmount = x EXPECT get xor is correctly set up`() {
        state = state.copy(xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor),
            state.getInsufficientXorText
        )

        Assert.assertEquals(false, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor),
            state.getInsufficientXorText
        )

        Assert.assertEquals(false, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_get_xor,
                payload = arrayOf(1f.toString())
            ),
            state.getInsufficientXorText
        )

        Assert.assertEquals(true, state.shouldGetInsufficientXorButtonBeShown)
    }

    /**
     * User has xor on account, but:
     *  1) we loan him xor(for some reason? possible bug on backend) -> do not show get xor button
     *  2) he needs 0 xor(for some reason? 0.00000001 for example) -> show get xor button
     *  3) he has to buy more xor -> show get xor button
     */
    @Test
    fun `xorCurrentAmount = 1, xorInsufficientAmount = x EXPECT get xor is correctly set up`() {
        state = state.copy(xorCurrentAmount = 1f, xorInsufficientAmount = -1f)

        Assert.assertEquals(
            Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor),
            state.getInsufficientXorText
        )

        Assert.assertEquals(false, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 0f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_get_xor,
                payload = arrayOf(0f.toString())
            ),
            state.getInsufficientXorText
        )

        Assert.assertEquals(true, state.shouldGetInsufficientXorButtonBeShown)

        state = state.copy(xorInsufficientAmount = 1f)

        Assert.assertEquals(
            Text.StringResWithArgs(
                id = R.string.card_issuance_screen_free_card_get_xor,
                payload = arrayOf(1f.toString())
            ),
            state.getInsufficientXorText
        )

        Assert.assertEquals(true, state.shouldGetInsufficientXorButtonBeShown)
    }
}