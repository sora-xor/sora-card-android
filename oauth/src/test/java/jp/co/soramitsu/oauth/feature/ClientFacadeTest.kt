package jp.co.soramitsu.oauth.feature

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.contract.IbanInfo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanStatus
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.clients.ClientsFacade
import jp.co.soramitsu.oauth.clients.VersionsDto
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.oauth.network.SoraCardNetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClientFacadeTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var sessionRepo: UserSessionRepository

    @MockK
    private lateinit var client: SoraCardNetworkClient

    @MockK
    private lateinit var kycRepository: KycRepository

    @MockK
    private lateinit var validator: AccessTokenValidator

    @MockK
    private lateinit var proxy: PWOAuthClientProxy

    @MockK
    private lateinit var memo: InMemoryRepo

    @MockK
    private lateinit var ctx: Context

    private lateinit var facade: ClientsFacade

    @Before
    fun setUp() {
        coEvery { proxy.logout() } just runs
        coEvery { sessionRepo.logOutUser() } just runs
        coEvery { kycRepository.getApplicationFee(any()) } returns "fee"
        coEvery {
            kycRepository.getIbanStatus(
                any(),
                any(),
            )
        } returns Result.success(
            IbanInfo(
                "iban",
                IbanStatus.ACTIVE,
                "234",
                "desc",
            ),
        )
        coEvery { proxy.init(any(), any(), any(), any(), any(), any()) } returns (true to "")
        every { memo.networkHeader } returns "header"
        every { memo.url(any(), any()) } returns "url"
        coEvery {
            client.get(
                header = "header",
                bearerToken = null,
                url = "url",
                deserializer = VersionsDto.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = VersionsDto(sora = "1.2", fearless = "4.5"),
            statusCode = 200,
            message = "",
        )
        coEvery { validator.checkAccessTokenValidity() } returns AccessTokenResponse.Token(
            "token2",
            123L,
        )
        facade = ClientsFacade(
            sessionRepo, client, kycRepository, validator, proxy, memo,
        )
    }

    @Test
    fun `test check`() = runTest {
        advanceUntilIdle()
        facade.logout()
        coVerify { proxy.logout() }
        coVerify { sessionRepo.logOutUser() }
    }

    @Test
    fun `test init app fee`() = runTest {
        advanceUntilIdle()
        initFacade()
        advanceUntilIdle()
        val fee = facade.getApplicationFee()
        coVerify { kycRepository.getApplicationFee("baseurl") }
        assertEquals("fee", fee)
    }

    @Test
    fun `test init iban`() = runTest {
        advanceUntilIdle()
        initFacade()
        advanceUntilIdle()
        val iban = facade.getIBAN()
        assertTrue(iban.isSuccess)
        assertEquals("iban", iban.getOrNull()!!.iban)
    }

    @Test
    fun `test init support version`() = runTest {
        advanceUntilIdle()
        initFacade()
        advanceUntilIdle()
        var ver = facade.getSoraSupportVersion()
        assertTrue(ver.isSuccess)
        assertEquals("1.2", ver.getOrNull()!!)
        ver = facade.getFearlessSupportVersion()
        assertTrue(ver.isSuccess)
        assertEquals("4.5", ver.getOrNull()!!)
    }

    @Test
    fun `test init`() = runTest {
        advanceUntilIdle()
        initFacade()
        coVerify {
            proxy.init(
                ctx,
                SoraCardEnvironmentType.TEST,
                "key",
                "domain",
                "platform",
                "recap",
            )
        }
    }

    private suspend fun initFacade() {
        facade.init(
            SoraCardBasicContractData(
                "key",
                "domain",
                SoraCardEnvironmentType.TEST,
                "platform",
                "recap",
            ),
            context = ctx,
            baseUrl = "baseurl",
        )
    }
}
