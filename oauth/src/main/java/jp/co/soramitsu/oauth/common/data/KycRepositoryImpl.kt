package jp.co.soramitsu.oauth.common.data

import java.util.UUID
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanInfo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanStatus
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.model.CountryCodeDto
import jp.co.soramitsu.oauth.common.model.CountryDial
import jp.co.soramitsu.oauth.common.model.FeesDto
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberRequest
import jp.co.soramitsu.oauth.common.model.GetReferenceNumberResponse
import jp.co.soramitsu.oauth.common.model.IbanAccountResponse.Companion.IBAN_ACCOUNT_ACTIVE_STATUS
import jp.co.soramitsu.oauth.common.model.IbanAccountResponse.Companion.IBAN_ACCOUNT_CLOSED_STATUS
import jp.co.soramitsu.oauth.common.model.IbanAccountResponseWrapper
import jp.co.soramitsu.oauth.common.model.KycAttemptsDto
import jp.co.soramitsu.oauth.common.model.KycResponse
import jp.co.soramitsu.oauth.common.model.KycStatus
import jp.co.soramitsu.oauth.common.model.VerificationStatus
import jp.co.soramitsu.oauth.common.model.XorEuroPrice
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun String.toDoubleNan(): Double? = this.toDoubleOrNull()?.let {
    if (it.isNaN()) null else it
}

class KycRepositoryImpl(
    private val apiClient: SoraCardNetworkClient,
    private val inMemoryRepo: InMemoryRepo,
    private val userSessionRepository: UserSessionRepository,
) : KycRepository {

    private var cacheReference: String = ""

    override suspend fun getReferenceNumber(
        accessToken: String,
        phoneNumber: String?,
        email: String?,
    ): Result<String> {
        if (cacheReference.isNotEmpty()) return Result.success(cacheReference)
        return runCatching {
            val ref = apiClient.post(
                header = inMemoryRepo.networkHeader,
                bearerToken = accessToken,
                url = inMemoryRepo.url(null, NetworkRequest.GET_REFERENCE_NUMBER),
                body = GetReferenceNumberRequest(
                    referenceID = UUID.randomUUID().toString(),
                    mobileNumber = phoneNumber,
                    email = email,
                    addressChanged = false,
                    documentChanged = false,
                    additionalData = "",
                ),
                deserializer = GetReferenceNumberResponse.serializer(),
            ).parse { value, _ ->
                checkNotNull(value) {
                    // Normally should not be encountered
                    "Failed - Internal error"
                }.referenceNumber
            }
            cacheReference = ref
            cacheReference
        }
    }

    private suspend fun getKycInfo(
        accessToken: String,
        baseUrl: String? = null,
    ): Result<KycResponse?> = runCatching {
        apiClient.get(
            header = inMemoryRepo.networkHeader,
            bearerToken = accessToken,
            url = inMemoryRepo.url(baseUrl, NetworkRequest.GET_KYC_LAST_STATUS),
            deserializer = KycResponse.serializer(),
        ).parse { value, _ ->
            checkNotNull(value) {
                // Normally should not be encountered
                "Failed - Internal error"
            }
        }
    }

    private var cacheKycResponse: Pair<SoraCardCommonVerification, KycResponse?>? = null

    override fun getCachedKycResponse(): Pair<SoraCardCommonVerification, KycResponse?>? {
        val local = cacheKycResponse
        cacheKycResponse = null
        return local
    }

    override suspend fun getIbanStatus(accessToken: String, baseUrl: String?): Result<IbanInfo?> =
        runCatching {
            val wrapper = apiClient.get(
                header = inMemoryRepo.networkHeader,
                bearerToken = accessToken,
                url = inMemoryRepo.url(baseUrl, NetworkRequest.GET_IBAN_DESC),
                deserializer = IbanAccountResponseWrapper.serializer(),
            ).parse { value, _ ->
                checkNotNull(value) {
                    // Normally should not be encountered
                    "Failed - Internal error"
                }
            }
            wrapper.ibans?.maxByOrNull { it.createdDate }?.let { response ->
                val bal = response.availableBalance.let {
                    "%s%.2f".format("€", it / 100.0)
                }
                IbanInfo(
                    iban = response.iban,
                    ibanStatus = when (response.status) {
                        IBAN_ACCOUNT_ACTIVE_STATUS -> IbanStatus.ACTIVE
                        IBAN_ACCOUNT_CLOSED_STATUS -> IbanStatus.CLOSED
                        else -> IbanStatus.OTHER
                    },
                    balance = bal,
                    statusDescription = response.statusDescription,
                )
            }
        }

    override suspend fun getKycLastFinalStatus(
        accessToken: String,
        baseUrl: String?,
    ): Result<SoraCardCommonVerification> {
        userSessionRepository.getKycStatus()?.let {
            if (it == SoraCardCommonVerification.Successful) {
                return Result.success(
                    SoraCardCommonVerification.Successful,
                )
            }
        }
        return getKycInfo(accessToken, baseUrl).map { kycStatus ->
            mapKycStatus(kycStatus).also {
                cacheKycResponse = it to kycStatus
                cacheReference = if (it == SoraCardCommonVerification.Rejected) {
                    ""
                } else {
                    kycStatus?.userReferenceNumber.orEmpty()
                }
                userSessionRepository.setKycStatus(it)
            }
        }
    }

    private fun mapKycStatus(kycResponse: KycResponse?): SoraCardCommonVerification {
        return when {
            kycResponse == null -> SoraCardCommonVerification.NotFound

            (kycResponse.verificationStatus == VerificationStatus.Accepted) -> {
                SoraCardCommonVerification.Successful
            }

            (kycResponse.kycStatus == KycStatus.Successful || kycResponse.kycStatus == KycStatus.Completed) -> {
                SoraCardCommonVerification.Pending
            }

            kycResponse.kycStatus == KycStatus.Failed -> {
                SoraCardCommonVerification.Failed
            }

            kycResponse.kycStatus == KycStatus.Started -> {
                SoraCardCommonVerification.Started
            }

            kycResponse.kycStatus == KycStatus.Retry -> {
                SoraCardCommonVerification.Retry
            }

            kycResponse.kycStatus == KycStatus.Rejected -> {
                SoraCardCommonVerification.Rejected
            }

            else -> SoraCardCommonVerification.NotFound
        }
    }

    override suspend fun hasFreeKycAttempt(accessToken: String): Result<Boolean> =
        getFreeKycAttemptsInfo(accessToken).map { it.freeAttemptAvailable }

    override suspend fun getFreeKycAttemptsInfo(accessToken: String): Result<KycAttemptsDto> {
        return runCatching {
            apiClient.get(
                header = inMemoryRepo.networkHeader,
                bearerToken = accessToken,
                url = inMemoryRepo.url(null, NetworkRequest.GET_KYC_FREE_ATTEMPT_INFO),
                deserializer = KycAttemptsDto.serializer(),
            ).parse { value, _ ->
                checkNotNull(value) {
                    // Normally should not be encountered
                    "Failed - Internal error"
                }
            }
        }
    }

    private var feesCache: Pair<String, String>? = null

    override suspend fun getRetryFee(): String =
        feesCache?.first ?: getFeesInternal().getOrNull()?.let {
            feesCache = it
            it.first
        } ?: ""

    override suspend fun getApplicationFee(baseUrl: String?): String =
        feesCache?.second ?: getFeesInternal(baseUrl).getOrNull()?.let {
            feesCache = it
            it.second
        } ?: ""

    private suspend fun getFeesInternal(baseUrl: String? = null): Result<Pair<String, String>> =
        runCatching {
            val dto = apiClient.get(
                header = inMemoryRepo.networkHeader,
                bearerToken = null,
                url = inMemoryRepo.url(baseUrl, NetworkRequest.FEES),
                deserializer = FeesDto.serializer(),
            ).parse { value, _ ->
                checkNotNull(value) {
                    // Normally should not be encountered
                    "Failed - Internal error"
                }
            }
            dto.retryFee to dto.applicationFee
        }

    override suspend fun getCurrentXorEuroPrice(accessToken: String): Result<Double> {
        return runCatching {
            apiClient.get(
                header = inMemoryRepo.networkHeader,
                bearerToken = accessToken,
                url = inMemoryRepo.url(null, NetworkRequest.GET_CURRENT_XOR_EURO_PRICE),
                deserializer = XorEuroPrice.serializer(),
            ).parse { value, _ ->
                checkNotNull(value) {
                    // Normally should not be encountered
                    "Failed - Internal error"
                }
            }
        }.mapCatching {
            it.price.toDoubleNan() ?: error("XOR Euro price failed")
        }
    }

    private val countriesCache = mutableListOf<CountryDial>()

    override suspend fun getCountries(baseUrl: String?): List<CountryDial> =
        countriesCache.takeIf { it.isNotEmpty() } ?: getCountriesInternal(baseUrl).also {
            countriesCache.clear()
            countriesCache.addAll(it)
        }

    private suspend fun getCountriesInternal(baseUrl: String?) = runCatching {
        val response = apiClient.get(
            header = inMemoryRepo.networkHeader,
            bearerToken = null,
            url = inMemoryRepo.url(baseUrl, NetworkRequest.COUNTRY_CODES),
            deserializer = MapSerializer(
                keySerializer = String.serializer(),
                valueSerializer = CountryCodeDto.serializer()
            )
        ).parse { value, _ ->
            checkNotNull(value) {
                // Normally should not be encountered
                "Failed - Internal error"
            }
        }

        response.map { (code, codeInfo) ->
            CountryDial(
                code = code,
                name = codeInfo.countryName,
                dialCode = codeInfo.dialCode,
            )
        }
    }.getOrDefault(emptyList())
}
