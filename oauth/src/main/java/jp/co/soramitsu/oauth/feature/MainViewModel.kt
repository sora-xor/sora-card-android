package jp.co.soramitsu.oauth.feature

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.kyc.android.sdk.data.model.PayWingsUserCredentials
import com.paywings.kyc.android.sdk.data.model.PayWingsWhiteLabelCredentials
import com.paywings.kyc.android.sdk.initializer.PayWingsKycClient
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardContractData
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
    private val mainRouter: MainRouter,
    val inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    private val tokenValidator: AccessTokenValidator,
    private val setActivityResult: SetActivityResult,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()

    private var soraCardContractData: SoraCardContractData? = null

    fun launch(contract: SoraCardContractData, activity: Activity) {
        soraCardContractData = contract
        inMemoryRepo.locale = contract.locale.country
        inMemoryRepo.environment = contract.basic.environment
        inMemoryRepo.soraBackEndUrl = contract.soraBackEndUrl
        inMemoryRepo.client = contract.client
        inMemoryRepo.userAvailableXorAmount = contract.userAvailableXorAmount
        inMemoryRepo.areAttemptsPaidSuccessfully = contract.areAttemptsPaidSuccessfully
        inMemoryRepo.isEnoughXorAvailable = contract.isEnoughXorAvailable
        inMemoryRepo.isIssuancePaid = contract.isIssuancePaid
        inMemoryRepo.logIn = contract.logIn

        viewModelScope.launch {
            val initResult = pwoAuthClientProxy.init(
                activity,
                contract.basic.environment,
                contract.basic.apiKey,
                contract.basic.domain,
                contract.basic.platform,
                contract.basic.recaptcha,
            )
            if (initResult.first) {
                if (pwoAuthClientProxy.isSignIn()) {
                    onAuthSucceed()
                } else {
                    if (userSessionRepository.isTermsRead()) {
                        navigateToSignIn()
                    } else {
                        mainRouter.openTermsAndConditions()
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    error = initResult.second,
                )
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(loading = loading)
    }

    private suspend fun checkAccessTokenValidity(
        onNewToken: suspend (accessToken: String) -> Unit,
    ) {
        when (val validity = tokenValidator.checkAccessTokenValidity()) {
            is AccessTokenResponse.AuthError -> {
                onError(validity.code)
            }

            AccessTokenResponse.SignInRequired -> {
                onUserSignInRequired()
            }

            is AccessTokenResponse.Token -> {
                onNewToken(validity.token)
            }

            null -> {}
        }
    }

    private fun onError(error: OAuthErrorCode) {
        showLoading(false)
        navigateToSignIn()
    }

    private fun onUserSignInRequired() {
        viewModelScope.launch {
            showLoading(false)
            pwoAuthClientProxy.logout()
            userSessionRepository.logOutUser()
            navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        mainRouter.openEnterPhoneNumber()
    }

    fun onAuthSucceed() {
        viewModelScope.launch {
            checkAccessTokenValidity { accessToken ->
                kycRepository.getIbanStatus(accessToken)
                    .onSuccess { info ->
                        if (info == null) {
                            onAuthSucceedNoIban(accessToken)
                        } else {
                            setActivityResult.setResult(
                                SoraCardResult.Success(SoraCardCommonVerification.IbanIssued),
                            )
                        }
                    }
                    .onFailure {
                        onAuthSucceedNoIban(accessToken)
                    }
            }
        }
    }

    private suspend fun onAuthSucceedNoIban(accessToken: String) {
        kycRepository.getKycLastFinalStatus(accessToken)
            .onSuccess { kycResponse ->
                when (kycResponse) {
                    SoraCardCommonVerification.Failed -> mainRouter.openGetPrepared()
                    else -> showKycStatusScreen(kycResponse)
                }
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    error = it.localizedMessage ?: "KYC status failed",
                )
            }
    }

    private fun checkKycRequirementsFulfilled() {
        mainRouter.openGetPrepared()
    }

    fun startKycProcess(activity: Activity) {
        val contract = soraCardContractData ?: return
        viewModelScope.launch {
            showLoading(true)
            val payWingsKycClient = PayWingsKycClient(
                activity = activity,
                whiteLabelCredentials = PayWingsWhiteLabelCredentials(
                    endpointUrl = contract.kycCredentials.endpointUrl,
                    username = contract.kycCredentials.username,
                    password = contract.kycCredentials.password,
                ),
                userCredentials = { mu, rm ->
                    runBlocking {
                        val authData = pwoAuthClientProxy.getNewAccessToken(mu, rm)
                        when {
                            authData.dpop.isNullOrBlank().not() &&
                                authData.accessTokenData != null ->
                                return@runBlocking PayWingsUserCredentials(
                                    dpop = authData.dpop!!,
                                    accessToken = authData.accessTokenData?.accessToken ?: "",
                                )

                            authData.errorData != null ->
                                return@runBlocking PayWingsUserCredentials(
                                    dpop = "",
                                    accessToken = "",
                                )

                            else -> {
                                navigateToSignIn()
                                return@runBlocking PayWingsUserCredentials(
                                    dpop = "",
                                    accessToken = "",
                                )
                            }
                        }
                    }
                },
                onSuccess = { _, _ ->
                    viewModelScope.launch(Dispatchers.Main) {
                        checkKycStatus()
                    }
                },
                onError = { _, _, _, code, errorMessage ->
                    viewModelScope.launch(Dispatchers.Main) {
                        onKycFailed(errorMessage ?: code.description)
                    }
                },
            )
            checkAccessTokenValidity { accessToken ->
                pwoAuthClientProxy.getUserData(
                    callback = object : GetUserDataCallback {
                        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
                            showLoading(false)
                            _uiState.value = _uiState.value.copy(
                                error = error.description,
                            )
                        }

                        override fun onUserSignInRequired() {
                            showLoading(false)
                            navigateToSignIn()
                        }

                        override fun onUserData(
                            userId: String,
                            firstName: String?,
                            lastName: String?,
                            email: String?,
                            emailConfirmed: Boolean,
                            phoneNumber: String?,
                        ) {
                            viewModelScope.launch {
                                kycRepository.getReferenceNumber(
                                    accessToken = accessToken,
                                    phoneNumber = phoneNumber,
                                    email = email,
                                ).onSuccess {
                                    showLoading(false)
                                    payWingsKycClient.startKyc(it)
                                }
                                    .onFailure {
                                        showLoading(false)
                                        _uiState.value = _uiState.value.copy(
                                            error = it.localizedMessage
                                                ?: "Error occurred while get-reference-number",
                                        )
                                    }
                            }
                        }
                    },
                )
            }
        }
    }

    private fun checkKycStatus() {
        viewModelScope.launch {
            showLoading(true)
            checkAccessTokenValidity { accessToken ->
                kycRepository.getKycLastFinalStatus(accessToken)
                    .onSuccess { status ->
                        showKycStatusScreen(status)
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(
                            error = it.localizedMessage ?: "KYC status not found",
                        )
                    }
                showLoading(false)
            }
        }
    }

    private fun showKycStatusScreen(kycResponse: SoraCardCommonVerification) {
        when {
            (kycResponse == SoraCardCommonVerification.Pending) -> {
                mainRouter.openVerificationInProgress()
            }

            (kycResponse == SoraCardCommonVerification.Successful) -> {
                mainRouter.openVerificationSuccessful()
            }

            (kycResponse == SoraCardCommonVerification.Retry) -> {
                mainRouter.openVerificationRejected()
            }

            (kycResponse == SoraCardCommonVerification.Started) -> {
                mainRouter.openGetPrepared()
            }

            (kycResponse == SoraCardCommonVerification.NotFound) -> {
                checkKycRequirementsFulfilled()
            }

            kycResponse == SoraCardCommonVerification.Failed -> {
                onKycFailed(SoraCardCommonVerification.Failed.name)
            }

            kycResponse == SoraCardCommonVerification.Rejected -> {
                mainRouter.openVerificationRejected()
            }
        }
    }

    private fun onKycFailed(statusDescription: String) {
        mainRouter.openVerificationFailed(additionalDescription = statusDescription)
    }

    fun onHideErrorDialog() {
        _uiState.value = _uiState.value.copy(
            error = null,
        )
    }
}
