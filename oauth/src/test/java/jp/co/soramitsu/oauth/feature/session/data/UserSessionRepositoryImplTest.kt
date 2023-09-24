package jp.co.soramitsu.oauth.feature.session.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import jp.co.soramitsu.oauth.base.data.SoraCardDataStore
import jp.co.soramitsu.oauth.domain.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
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

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var soraCardDataStore: SoraCardDataStore

    private lateinit var repository: UserSessionRepositoryImpl

    @Before
    fun setUp() {
        coEvery { soraCardDataStore.putString(any(), any()) } just runs
        coEvery { soraCardDataStore.putLong(any(), any()) } just runs
        repository = UserSessionRepositoryImpl(soraCardDataStore)
    }

    @Test
    fun `signIn user EXPECT update refresh token`() = runTest {
        coEvery { soraCardDataStore.getString(REFRESH_TOKEN_KEY) } returns REFRESH_TOKEN_VALUE

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        coVerify { soraCardDataStore.putString(REFRESH_TOKEN_KEY, REFRESH_TOKEN_VALUE) }
        assertEquals(REFRESH_TOKEN_VALUE, repository.getRefreshToken())
    }

    @Test
    fun `signIn user EXPECT update access token`() = runTest {
        coEvery { soraCardDataStore.getString(ACCESS_TOKEN_KEY) } returns ACCESS_TOKEN_VALUE

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        coVerify { soraCardDataStore.putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALUE) }
        assertEquals(ACCESS_TOKEN_VALUE, repository.getAccessToken())
    }

    @Test
    fun `signIn user EXPECT update expiration time`() = runTest {
        coEvery {
            soraCardDataStore.getLong(
                ACCESS_TOKEN_EXPIRATION_TIME_KEY,
                0
            )
        } returns EXPIRATION_TIME_VALUE

        repository.signInUser(
            REFRESH_TOKEN_VALUE,
            ACCESS_TOKEN_VALUE,
            EXPIRATION_TIME_VALUE
        )

        coVerify {
            soraCardDataStore.putLong(
                ACCESS_TOKEN_EXPIRATION_TIME_KEY,
                EXPIRATION_TIME_VALUE
            )
        }
        assertEquals(EXPIRATION_TIME_VALUE, repository.getAccessTokenExpirationTime())
    }

    @Test
    fun `set new access token EXPECT update the token`() = runTest {
        repository.setNewAccessToken(ACCESS_TOKEN_VALUE, EXPIRATION_TIME_VALUE)

        coVerify { soraCardDataStore.putString(ACCESS_TOKEN_KEY, ACCESS_TOKEN_VALUE) }
        coVerify {
            soraCardDataStore.putLong(
                ACCESS_TOKEN_EXPIRATION_TIME_KEY,
                EXPIRATION_TIME_VALUE
            )
        }
    }
}
