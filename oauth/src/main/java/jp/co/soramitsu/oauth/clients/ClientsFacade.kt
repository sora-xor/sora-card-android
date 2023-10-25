package jp.co.soramitsu.oauth.clients

import android.content.Context
import io.ktor.client.call.body
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardBasicContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.common.model.IbanAccountResponseWrapper
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.NetworkRequest
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import javax.inject.Inject
import javax.inject.Singleton

class SoraCardTokenException(val type: String) : IllegalStateException("No valid soracard token: $type")

@Singleton
class ClientsFacade @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val apiClient: SoraCardNetworkClient,
    private val kycRepository: KycRepository,
    private val tokenValidator: AccessTokenValidator,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) {
    companion object {
        const val techSupport = "techsupport@soracard.com"
    }
    private var baseUrl: String? = null

    suspend fun logout() {
        userSessionRepository.logOutUser()
    }

    fun init(contract: SoraCardBasicContractData, context: Context, baseUrl: String) {
        this.baseUrl = baseUrl
        pwoAuthClientProxy.init(context, contract.environment, contract.apiKey, contract.domain)
    }

    suspend fun getApplicationFee(): String = kycRepository.getApplicationFee(baseUrl)

    suspend fun getKycStatus(): Result<SoraCardCommonVerification> {
        return when (val validity = tokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> {
                kycRepository.getKycLastFinalStatus(validity.token, baseUrl)
            }
            else -> Result.failure(SoraCardTokenException("KYC status"))
        }
    }

    suspend fun getIBAN(): Result<IbanAccountResponseWrapper> {
        return when (val validity = tokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.Token -> {
                runCatching {
                    apiClient.get(
                        validity.token,
                        NetworkRequest.GET_IBAN_DESC.url,
                        baseUrl,
                    ).body()
                }
            }

            else -> Result.failure(SoraCardTokenException("IBAN"))
        }
    }
}
