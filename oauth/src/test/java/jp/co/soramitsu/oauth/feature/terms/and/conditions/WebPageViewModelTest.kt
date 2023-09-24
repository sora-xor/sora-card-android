package jp.co.soramitsu.oauth.feature.terms.and.conditions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import jp.co.soramitsu.oauth.feature.terms.and.conditions.model.WebUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class WebPageViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var mainRouter: MainRouter

    private lateinit var viewModel: WebPageViewModel

    @Before
    fun setUp() {
        every { mainRouter.back() } just runs
        viewModel = WebPageViewModel(mainRouter)
    }

    @Test
    fun `set args EXPECT update toolbar title`() {
        viewModel.setArgs("Title", "GENERAL_TERMS")

        assertEquals("Title", viewModel.toolbarState.value?.basic?.title)
    }

    @Test
    fun `set args EXPECT update state`() {
        viewModel.setArgs("Title", "GENERAL_TERMS")

        assertEquals(WebUrl.GENERAL_TERMS.url, viewModel.state.value.url)
    }

    @Test
    fun `back EXPECT navigate back`() {
        viewModel.onToolbarNavigation()
        verify { mainRouter.back() }
    }
}