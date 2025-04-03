package jp.co.soramitsu.oauth.feature.getmorexor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class GetMoreXorViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var setActivityResult: SetActivityResult

    @MockK
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: GetMoreXorViewModel

    @Before
    fun setUp() {
        every { setActivityResult.setResult(any()) } just runs
        every { mainRouter.back() } just runs
        viewModel = GetMoreXorViewModel(
            mainRouter = mainRouter,
            setActivityResult = setActivityResult,
        )
    }

    @Test
    fun `call onPurchaseMethodClicked EXPECT set activity result is called`() {
        XorPurchaseMethod.values().forEachIndexed { index, xorPurchaseMethod ->
            viewModel.onPurchaseMethodClicked(index)
            verify { setActivityResult.setResult(xorPurchaseMethod.mapToSoraCardNavigation()) }
        }
    }

    @Test
    fun `cancel dialog EXPECT back press behavior`() {
        viewModel.onCancelDialogClicked()
        verify { mainRouter.back() }
    }
}
