package jp.co.soramitsu.oauth.feature.session.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.oauth.core.engines.SoraCardDataStore
import jp.co.soramitsu.oauth.base.test.MainCoroutineRule
import jp.co.soramitsu.oauth.core.datasources.session.impl.UserSessionRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class UserSessionRepositoryImplTest {

    private companion object {
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val ACCESS_TOKEN_EXPIRATION_TIME_KEY = "ACCESS_TOKEN_EXPIRATION_TIME_KEY"

        const val REFRESH_TOKEN_VALUE = "REFRESH_TOKEN"
        const val ACCESS_TOKEN_VALUE = "ACCESS_TOKEN"

        const val EXPIRATION_TIME_VALUE = Long.MAX_VALUE

        const val NEW_ACCESS_TOKEN = "NEW_ACCESS_TOKEN"
        const val NEW_EXPIRATION_TIME_VALUE = Long.MAX_VALUE / 2
    }

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var soraCardDataStore: SoraCardDataStore

    private lateinit var repository: UserSessionRepositoryImpl

    @Before
    fun setUp() {
        repository = UserSessionRepositoryImpl(soraCardDataStore)
    }

    @Test
    fun `signIn user EXPECT update refresh token`() = runTest {
        whenever(soraCardDataStore.getString(REFRESH_TOKEN_KEY)).thenReturn(REFRESH_TOKEN_VALUE)

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        verify(soraCardDataStore).putString(REFRESH_TOKEN_KEY, REFRESH_TOKEN_VALUE)
        assertEquals(REFRESH_TOKEN_VALUE, repository.getRefreshToken())
    }

    @Test
    fun `signIn user EXPECT update access token`() = runTest {
        whenever(soraCardDataStore.getString(ACCESS_TOKEN_KEY)).thenReturn(ACCESS_TOKEN_VALUE)

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        verify(soraCardDataStore).putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALUE)
        assertEquals(ACCESS_TOKEN_VALUE, repository.getAccessToken())
    }

    @Test
    fun `signIn user EXPECT update expiration time`() = runTest {
        whenever(soraCardDataStore.getLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, 0))
            .thenReturn(EXPIRATION_TIME_VALUE)

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        verify(soraCardDataStore).putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, EXPIRATION_TIME_VALUE)
        assertEquals(EXPIRATION_TIME_VALUE, repository.getAccessTokenExpirationTime())
    }

    @Test
    fun `set new access token EXPECT update the token`() = runTest {
        repository.setNewAccessToken(ACCESS_TOKEN_VALUE, EXPIRATION_TIME_VALUE)

        verify(soraCardDataStore).putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALUE)
        verify(soraCardDataStore).putLong(ACCESS_TOKEN_EXPIRATION_TIME_KEY, EXPIRATION_TIME_VALUE)
    }
}
