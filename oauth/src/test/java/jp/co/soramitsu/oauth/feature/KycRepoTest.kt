package jp.co.soramitsu.oauth.feature

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanStatus
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.data.KycRepositoryImpl
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.CountryCodeDto
import jp.co.soramitsu.oauth.common.model.FeesDto
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.common.model.IbanAccountResponse
import jp.co.soramitsu.oauth.common.model.IbanAccountResponseWrapper
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.common.model.VerificationStatus
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.oauth.network.SoraCardNetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KycRepoTest {

    @MockK
    private lateinit var client: SoraCardNetworkClient

    @MockK
    private lateinit var memo: InMemoryRepo

    @MockK
    private lateinit var session: UserSessionRepository

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var kyc: KycRepository

    @Before
    fun setUp() {
        every { memo.networkHeader } returns "header"
        every { memo.url(any(), any()) } returns "url"
        coEvery { session.setKycStatus(any()) } just runs
        kyc = KycRepositoryImpl(
            client, memo, session,
        )
    }

    @Test
    fun `test final status cache success`() = runTest {
        coEvery { session.getKycStatus() } returns SoraCardCommonVerification.Successful
        val fs = kyc.getKycLastFinalStatus("token", "url")
        assertTrue(fs.isSuccess)
        assertEquals(SoraCardCommonVerification.Successful, fs.getOrNull()!!)
    }

    @Test
    fun `retry fee`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = null,
                url = "url",
                deserializer = FeesDto.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = FeesDto(applicationFee = "23", retryFee = "99"),
            statusCode = 200,
            message = "",
        )
        val fee = kyc.getRetryFee()
        assertEquals("99", fee)
    }

    @Test
    fun `xor euro`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = "token",
                url = "url",
                deserializer = XorEuroPrice.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = XorEuroPrice(pair = "", price = "34.92", source = "", timeOfUpdate = 123L),
            statusCode = 200,
            message = "",
        )
        val pr = kyc.getCurrentXorEuroPrice("token")
        assertTrue(pr.isSuccess)
        assertEquals(34.92, pr.getOrNull()!!, 0.001)
    }

    @Test
    fun `test fee attempts`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = "token",
                url = "url",
                deserializer = KycAttemptsDto.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = KycAttemptsDto(
                total = 3,
                completed = 2,
                retry = 0,
                rejected = 1,
                freeAttemptsCount = 8,
                freeAttemptAvailable = true,
                successful = 2,
                totalFreeAttemptsCount = 9,
            ),
            statusCode = 200,
            message = "",
        )
        val att = kyc.hasFreeKycAttempt("token")
        assertTrue(att.isSuccess)
        assertEquals(true, att.getOrNull()!!)
    }

    @Test
    fun `test iban status`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = "token",
                url = "url",
                deserializer = IbanAccountResponseWrapper.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = IbanAccountResponseWrapper(
                referenceID = "",
                callerReferenceID = "",
                ibans = listOf(
                    IbanAccountResponse(
                        id = "id",
                        iban = "IBAN_876",
                        bicSwift = "",
                        bicSwiftForSepa = "",
                        bicSwiftForSwift = "",
                        description = "",
                        currency = "",
                        createdDate = "2024-03-07",
                        status = "A",
                        statusDescription = "sta des",
                        minTransactionAmount = 123L,
                        maxTransactionAmount = 88L,
                        balance = 650,
                        availableBalance = 298,
                    ),
                ),
                statusCode = 1,
                statusDescription = "d",
            ),
            statusCode = 200,
            message = "",
        )
        val iban = kyc.getIbanStatus("token", "url")
        assertTrue(iban.isSuccess)
        assertEquals("IBAN_876", iban.getOrNull()!!.iban)
        assertEquals(IbanStatus.ACTIVE, iban.getOrNull()!!.ibanStatus)
        assertEquals("€2.98", iban.getOrNull()!!.balance)
    }

    @Test
    fun `test final status success`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = "token",
                url = "url",
                deserializer = KycResponse.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = KycResponse(
                kycID = "",
                personID = "",
                userReferenceNumber = "",
                referenceID = "",
                kycStatus = KycStatus.Completed,
                verificationStatus = VerificationStatus.Pending,
                ibanStatus = jp.co.soramitsu.oauth.common.model.IbanStatus.Pending,
                updateTime = 123,
                additionalDescription = "",
                rejectionReasons = null,
            ),
            statusCode = 200,
            message = "",
        )
        coEvery { session.getKycStatus() } returns SoraCardCommonVerification.Pending
        val fs = kyc.getKycLastFinalStatus("token", "url")
        assertTrue(fs.isSuccess)
        coVerify { session.setKycStatus(SoraCardCommonVerification.Pending) }
        assertEquals(SoraCardCommonVerification.Pending, fs.getOrNull()!!)
    }

    @Test
    fun `test countries`() = runTest {
        coEvery {
            client.get(
                header = "header",
                bearerToken = null,
                url = "url",
                deserializer = any<KSerializer<Map<String, CountryCodeDto>>>(),
            )
        } returns SoraCardNetworkResponse(
            value = mapOf(
                "ru" to CountryCodeDto("Russia", "+7"),
                "br" to CountryCodeDto("Brazil", "+55"),
            ),
            statusCode = 200,
            message = null,
        )
        val res = kyc.getCountries()
        assertEquals(2, res.size)
    }

    @Test
    fun `test GetReferenceNumberResponse`() = runTest {
        coEvery {
            client.post(
                header = "header",
                bearerToken = "token",
                url = "url",
                body = any(),
                deserializer = GetReferenceNumberResponse.serializer(),
            )
        } returns SoraCardNetworkResponse(
            value = GetReferenceNumberResponse(
                referenceID = "",
                callerReferenceID = "",
                referenceNumber = "ref num",
                statusCode = 2,
                statusDescription = "",
            ),
            statusCode = 200,
            message = null,
        )
        val r = kyc.getReferenceNumber(
            accessToken = "token",
            phoneNumber = "123",
            email = "email",
        )
        advanceUntilIdle()
        assertTrue(r.isSuccess)
        val res = r.getOrNull()
        assertTrue(res != null)
        assertEquals("ref num", res!!.second)
        assertTrue(res.first)
    }
}
