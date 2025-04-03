package jp.co.soramitsu.oauth.feature.gatehub

import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.clients.SoraCardTokenException
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.oauth.network.SoraCardNetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GateHubRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var nc: SoraCardNetworkClient

    @MockK
    private lateinit var atv: AccessTokenValidator

    @MockK
    private lateinit var imr: InMemoryRepo

    private lateinit var repo: GateHubRepository

    @Before
    fun setUp() {
        every { imr.networkHeader } returns "nethed"
        every { imr.url(any(), any()) } returns "some url"
        repo = GateHubRepository(
            apiClient = nc,
            accessTokenValidator = atv,
            inMemoryRepo = imr,
        )
    }

    @Test
    fun `test get onboard fail`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.AuthError(code = OAuthErrorCode.UNKNOWN_ERROR)
        advanceUntilIdle()
        val r = repo.onboardUser()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is SoraCardTokenException)
    }

    @Test
    fun `test get onboard success`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        advanceUntilIdle()
        every { imr.ghExpectedExchangeVolume } returns 123
        every { imr.ghEmploymentStatus } returns 9
        every { imr.ghExchangeReason } returns listOf(2, 7)
        every { imr.ghSourceOfFunds } returns listOf(8, 2)
        every { imr.ghCountriesFrom } returns emptyList()
        every { imr.ghCountriesTo } returns emptyList()
        coEvery {
            nc.post(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardResponse.serializer(),
                url = "some url",
                body = Json.encodeToString(
                    OnboardRequestBody(
                        employmentStatus = 9,
                        expectedVolume = 123,
                        openingReason = listOf(2, 7),
                        sourceOfFunds = listOf(8, 2),
                        crossBorderDestinationCountries = null,
                        crossBorderOriginCountries = null,
                    ),
                ),
            )
        } returns SoraCardNetworkResponse(
            value = OnboardResponse(crid = "crid", rid = "rid", sc = 55, sd = "sd"),
            statusCode = 200,
        )
        val r = repo.onboardUser()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
    }

    @Test
    fun `test get iframe fail`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.AuthError(code = OAuthErrorCode.UNKNOWN_ERROR)
        advanceUntilIdle()
        val r = repo.getIframe()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is SoraCardTokenException)
    }

    @Test
    fun `test get iframe fail wrong url`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        advanceUntilIdle()
        coEvery {
            nc.post(
                header = "nethed",
                bearerToken = "123",
                deserializer = GetIframeResponse.serializer(),
                url = "some url",
                body = Json.encodeToString(GetIframeRequestBody(2)),
            )
        } returns SoraCardNetworkResponse(
            value = GetIframeResponse(crid = "crid", rif = "rif", sc = 2, sd = "sd", url = null),
            statusCode = 200,
        )
        val r = repo.getIframe()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is IllegalStateException)
        assertEquals("Failed - GetIframe|Url is not valid", (res as IllegalStateException).message)
    }

    @Test
    fun `test get iframe fail null value`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        advanceUntilIdle()
        coEvery {
            nc.post(
                header = "nethed",
                bearerToken = "123",
                deserializer = GetIframeResponse.serializer(),
                url = "some url",
                body = Json.encodeToString(GetIframeRequestBody(2)),
            )
        } returns SoraCardNetworkResponse(
            value = null,
            statusCode = 200,
        )
        val r = repo.getIframe()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is IllegalStateException)
        assertEquals("Failed - GetIframe|Null value", (res as IllegalStateException).message)
    }

    @Test
    fun `test get iframe failed no auth`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        advanceUntilIdle()
        coEvery {
            nc.post(
                header = "nethed",
                bearerToken = "123",
                deserializer = GetIframeResponse.serializer(),
                url = "some url",
                body = Json.encodeToString(GetIframeRequestBody(2)),
            )
        } returns SoraCardNetworkResponse(
            value = null,
            statusCode = 401,
        )
        val r = repo.getIframe()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is IllegalStateException)
        assertEquals("Failed - GetIframe|Unauthorised", (res as IllegalStateException).message)
    }

    @Test
    fun `test get iframe successful`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        advanceUntilIdle()
        coEvery {
            nc.post(
                header = "nethed",
                bearerToken = "123",
                deserializer = GetIframeResponse.serializer(),
                url = "some url",
                body = Json.encodeToString(GetIframeRequestBody(2)),
            )
        } returns SoraCardNetworkResponse(
            value = GetIframeResponse(crid = "crid", rif = "rif", sc = 2, sd = "sd", url = "url"),
            statusCode = 200,
        )
        val r = repo.getIframe()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
    }

    @Test
    fun `test onboarded fail`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.AuthError(code = OAuthErrorCode.UNKNOWN_ERROR)
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isFailure)
    }

    @Test
    fun `test onboarded accepted`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = OnboardedResponse(pid = "pid", vs = 1, vm = "vm", vd = "vd", ut = 3),
            statusCode = 200,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
        assertTrue(res!! is OnboardedResult.Accepted)
    }

    @Test
    fun `test onboarded rejected`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = OnboardedResponse(pid = "pid", vs = 2, vm = "vm", vd = "vd", ut = 3),
            statusCode = 200,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
        assertTrue(res!! is OnboardedResult.Rejected)
    }

    @Test
    fun `test onboarded pending`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = OnboardedResponse(pid = "pid", vs = 0, vm = "vm", vd = "vd", ut = 3),
            statusCode = 200,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
        assertTrue(res!! is OnboardedResult.Pending)
    }

    @Test
    fun `test onboarded 401`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = null,
            statusCode = 401,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is IllegalStateException)
        assertEquals(
            "Failed - Onboarded|Unauthorised (401)",
            (res as IllegalStateException).message,
        )
    }

    @Test
    fun `test onboarded 404`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = null,
            statusCode = 404,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
        assertTrue(res!! is OnboardedResult.OnboardingNotFound)
    }

    @Test
    fun `test onboarded 408`() = runTest {
        coEvery {
            atv.checkAccessTokenValidity()
        } returns AccessTokenResponse.Token(token = "123", expirationTime = 123L)
        coEvery {
            nc.get(
                header = "nethed",
                bearerToken = "123",
                deserializer = OnboardedResponse.serializer(),
                url = "some url",
            )
        } returns SoraCardNetworkResponse(
            value = null,
            statusCode = 408,
        )
        advanceUntilIdle()
        val r = repo.onboarded()
        assertTrue(r.isFailure)
        val res = r.exceptionOrNull()
        assertTrue(res != null)
        assertTrue(res!! is IllegalStateException)
        assertEquals(
            "Failed - Onboarded|Internal error (408)",
            (res as IllegalStateException).message,
        )
    }
}
