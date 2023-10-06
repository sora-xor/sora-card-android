package jp.co.soramitsu.oauth.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.SingleLiveEvent
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.model.AccessTokenResponse
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.CompatibilityDestination
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
    private val mainRouter: MainRouter,
    val inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    @KycRequirementsUnfulfilledFlow private val kycRequirementsUnfulfilledFlow: NavigationFlow,
    private val tokenValidator: AccessTokenValidator,
) : BaseViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState?> = _state.asStateFlow()

    private val _toast = SingleLiveEvent<String>()
    val toast: LiveData<String> = _toast

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val data = userSessionRepository.getUser()
            if (data.first.isEmpty() || data.second.isEmpty()) return@launch
            showLoading(loading = true)

            checkAccessTokenValidity { accessToken ->
                onAuthSucceed(accessToken)
                showLoading(false)
            }
        }
    }

    private val getUserDataCallback = object : GetUserDataCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            dialogState = DialogAlertState(
                title = error.name,
                message = error.description,
                dismissAvailable = true,
                onPositive = {
                    dialogState = null
                }
            )
        }

        override fun onUserData(
            userId: String,
            firstName: String?,
            lastName: String?,
            email: String?,
            emailConfirmed: Boolean,
            phoneNumber: String?
        ) {
            _state.value = _state.value.copy(
                kycUserData = KycUserData(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    mobileNumber = phoneNumber,
                )
            )

            viewModelScope.launch {
                userSessionRepository.setUserId(userId)
                tryCatch {
                    val accessToken = userSessionRepository.getAccessToken()
                    kycRepository.getReferenceNumber(
                        accessToken = accessToken,
                        phoneNumber = phoneNumber,
                        email = email,
                    ).onSuccess {
                        _state.value = _state.value.copy(
                            referenceNumber = it,
                        )
                    }
                        .onFailure {
                            _toast.value =
                                it.localizedMessage ?: "Error occurred while get-reference-number"
                        }
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            showLoading(true)
            checkAccessTokenValidity { accessToken ->
                val refreshToken = userSessionRepository.getRefreshToken()
                _state.value = _state.value.copy(
                    userCredentials = UserCredentials(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )

                pwoAuthClientProxy.getUserData(
                    accessToken = accessToken,
                    callback = getUserDataCallback,
                )
                showLoading(false)
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(loading = loading)
    }

    private suspend fun checkAccessTokenValidity(
        onNewToken: suspend (accessToken: String) -> Unit
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
        if (error == OAuthErrorCode.MISSING_REFRESH_TOKEN) {
            navigateToSignIn()
        }
    }

    private fun onUserSignInRequired() {
        viewModelScope.launch {
            showLoading(false)
            userSessionRepository.logOutUser()
            navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        mainRouter.openEnterPhoneNumber()
    }

    fun onAuthSucceed(accessToken: String) {
        viewModelScope.launch {
            kycRepository.getKycLastFinalStatus(accessToken).onSuccess { kycResponse ->
                when (kycResponse) {
                    SoraCardCommonVerification.Rejected -> checkKycRequirementsFulfilled(accessToken)
                    SoraCardCommonVerification.Failed -> mainRouter.openGetPrepared()
                    else -> showKycStatusScreen(kycResponse)
                }
            }
                .onFailure {
                    _toast.value = it.localizedMessage.orEmpty()
                }
        }
    }

    private fun checkKycRequirementsFulfilled(accessToken: String) {
        if (inMemoryRepo.isEnoughXorAvailable) {
            showKycStatusScreen(SoraCardCommonVerification.Rejected)
        } else {
            kycRequirementsUnfulfilledFlow.start(
                fromDestination = CompatibilityDestination(Destination.ENTER_PHONE_NUMBER.route)
            )
        }
    }

    fun checkKycStatus() {
        viewModelScope.launch {
            showLoading(true)
            checkAccessTokenValidity { accessToken ->
                kycRepository.getKycLastFinalStatus(accessToken).onSuccess { status ->
                    showKycStatusScreen(status)
                }
                    .onFailure {
                        _toast.value = it.localizedMessage.orEmpty()
                    }
                showLoading(false)
            }
        }
    }

    private fun showKycStatusScreen(
        kycResponse: SoraCardCommonVerification,
        statusDescription: String? = null
    ) {
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

            kycResponse == SoraCardCommonVerification.Failed -> {
                onKycFailed(statusDescription)
            }

            kycResponse == SoraCardCommonVerification.Rejected -> {
                mainRouter.openVerificationRejected()
            }
        }
    }

    fun onKycFailed(statusDescription: String?) {
        mainRouter.openVerificationFailed(additionalDescription = statusDescription)
    }
}
