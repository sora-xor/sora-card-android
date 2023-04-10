package jp.co.soramitsu.oauth.feature.get.prepared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.OAuthCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetPreparedViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mainRouter: MainRouter

    @Mock
    private lateinit var authCallback: OAuthCallback

    private lateinit var viewModel: GetPreparedViewModel

    @Before
    fun setUp() {
        viewModel = GetPreparedViewModel(
            mainRouter
        )
    }

    @Test
    fun `init EXPECT toolbar title`() {
        assertEquals(R.string.get_prepared_title, viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `init EXPECT set steps`() {
        assertEquals(TestData.STEPS, viewModel.state.steps)
    }

    @Test
    fun `on confirm EXPECT start kyc`() {
        viewModel.setArgs(authCallback)
        viewModel.onConfirm()

        verify(authCallback).onStartKyc()
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()

        verify(mainRouter).back()
    }
}
