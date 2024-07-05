package jp.co.soramitsu.oauth.clients

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.IbanInfo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import kotlinx.coroutines.CompletableDeferred

class SoraCardTokenException(val type: String) : IllegalStateException(
    "No valid soracard token: $type",
)

@Singleton
class ClientsFacade @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val apiClient: SoraCardNetworkClient,
    private val kycRepository: KycRepository,
    private val tokenValidator: AccessTokenValidator,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    private val inMemoryRepo: InMemoryRepo,
) {
    companion object {
        const val TECH_SUPPORT = "techsupport@soracard.com"
    }

    private var baseUrl: String? = null
    private val initDeferred: CompletableDeferred<Boolean> = CompletableDeferred()

    suspend fun logout() {
        pwoAuthClientProxy.logout()
        userSessionRepository.logOutUser()
    }

    suspend fun init(
        contract: SoraCardBasicContractData,
        context: Context,
        baseUrl: String,
    ): Pair<Boolean, String> {
        this.baseUrl = baseUrl
        return pwoAuthClientProxy.init(
            context,
            contract.environment,
            contract.apiKey,
            contract.domain,
            contract.platform,
            contract.recaptcha,
        ).also {
            initDeferred.complete(it.first)
        }
    }

    suspend fun getApplicationFee(): String {
        initDeferred.await()
        return kycRepository.getApplicationFee(baseUrl)
    }

    suspend fun getFearlessSupportVersion(): Result<String> {
        initDeferred.await()
        return getSupportVersion().map { it.fearless }
    }

    suspend fun getSoraSupportVersion(): Result<String> {
        initDeferred.await()
        return getSupportVersion().map { it.sora }
    }

    private suspend fun getSupportVersion() = runCatching {
        apiClient.get(
            header = inMemoryRepo.networkHeader,
            bearerToken = null,
            url = inMemoryRepo.url(baseUrl, NetworkRequest.VERSION),
            deserializer = VersionsDto.serializer(),
        ).parse { value, _ ->
            checkNotNull(value) {
                // Normally should not be encountered
                "Failed - Internal error"
            }
        }
    }

    suspend fun getKycStatus(): Result<SoraCardCommonVerification> {
        val init = initDeferred.await()
        if (init.not()) return Result.failure(SoraCardTokenException("OAuth init failed (KYC)"))
        return when (val validity = tokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> {
                kycRepository.getKycLastFinalStatus(validity.token, baseUrl)
            }

            else -> Result.failure(SoraCardTokenException("KYC status"))
        }
    }

    suspend fun getPhoneNumber(): String = userSessionRepository.getPhoneNumber()

    suspend fun getIBAN(): Result<IbanInfo?> {
        val init = initDeferred.await()
        if (init.not()) return Result.failure(SoraCardTokenException("OAuth init failed (IBAN)"))
        return when (val validity = tokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> {
                kycRepository.getIbanStatus(validity.token, baseUrl)
            }

            else -> Result.failure(SoraCardTokenException("IBAN"))
        }
    }
}
