package jp.co.soramitsu.oauth.feature

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import com.paywings.onboarding.kyc.android.sdk.data.model.KycUserData
import com.paywings.onboarding.kyc.android.sdk.data.model.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.Destination
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.destinations.CompatibilityDestination
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
    private val mainRouter: MainRouter,
    val inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    @KycRequirementsUnfulfilledFlow private val kycRequirementsUnfulfilledFlow: NavigationFlow,
) : BaseViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState?> = _state.asStateFlow()

    var uiState by mutableStateOf(MainScreenUiState())
        private set

    init {
        viewModelScope.launch {
            showLoading(loading = true)

            checkAccessTokenValidity { accessToken, accessTokenExpirationTime ->
                updateAccessToken(accessToken, accessTokenExpirationTime)

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
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            showLoading(true)
            checkAccessTokenValidity { accessToken, accessTokenExpirationTime ->
                updateAccessToken(accessToken, accessTokenExpirationTime)
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
        uiState = uiState.copy(loading = loading)
    }

    private suspend fun checkAccessTokenValidity(
        onNewToken: suspend (accessToken: String, accessTokenExpirationTime: Long) -> Unit
    ) {
        val accessToken = userSessionRepository.getAccessToken()
        val accessTokenExpirationTime = userSessionRepository.getAccessTokenExpirationTime()
        val accessTokenExpired =
            accessTokenExpirationTime < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        if (accessToken.isBlank() || accessTokenExpired) {
            getNewAccessToken(onNewToken)
        } else {
            onNewToken(accessToken, accessTokenExpirationTime)
        }
    }

    private suspend fun getNewAccessToken(
        onNewToken: suspend (accessToken: String, accessTokenExpirationTime: Long) -> Unit
    ) {
        val refreshToken = userSessionRepository.getRefreshToken()

        pwoAuthClientProxy.getNewAccessToken(
            refreshToken = refreshToken,
            callback = RefreshTokenCallbackWrapper(
                onNewAccessToken = { accessToken, accessTokenExpirationTime ->
                    viewModelScope.launch { onNewToken(accessToken, accessTokenExpirationTime) }
                },
                onError = this@MainViewModel::onError,
                onUserSignInRequired = this@MainViewModel::onUserSignInRequired
            ).getNewAccessTokenCallback,
        )
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

    private suspend fun updateAccessToken(accessToken: String, accessTokenExpirationTime: Long) {
        userSessionRepository.setNewAccessToken(accessToken, accessTokenExpirationTime)
    }

    private fun navigateToSignIn() {
        mainRouter.openEnterPhoneNumber()
    }

    fun onAuthSucceed(accessToken: String) {
        viewModelScope.launch {
            kycRepository.getKycLastFinalStatus(accessToken).onSuccess { kycResponse ->
                if (kycResponse != null
                    && (kycResponse == SoraCardCommonVerification.Rejected ||
                        kycResponse == SoraCardCommonVerification.Pending ||
                        kycResponse == SoraCardCommonVerification.Successful)
                ) {
                    showKycStatusScreen(kycResponse)
                } else {
                    checkKycRequirementsFulfilled(accessToken)
                }
            }
                .onFailure {
                    dialogState = DialogAlertState(
                        title = "Network Error",
                        message = it.localizedMessage,
                        dismissAvailable = true,
                        onPositive = {
                            dialogState = null
                        }
                    )
                }
        }
    }

    private suspend fun checkKycRequirementsFulfilled(accessToken: String) {
        kycRepository.hasFreeKycAttempt(accessToken)
            .onFailure {
                dialogState = DialogAlertState(
                    title = "Network Error",
                    message = it.localizedMessage,
                    dismissAvailable = true,
                    onPositive = {
                        dialogState = null
                    }
                )
            }
            .onSuccess { hasFreeAttempt ->
                if (inMemoryRepo.isEnoughXorAvailable) {
                    if (hasFreeAttempt) {
                        mainRouter.openGetPrepared()
                    } else {
                        showKycStatusScreen(SoraCardCommonVerification.Rejected)
                    }
                } else {
                    kycRequirementsUnfulfilledFlow.start(
                        fromDestination = CompatibilityDestination(Destination.ENTER_PHONE_NUMBER.route)
                    )
                }
            }
    }

    fun checkKycStatus(statusDescription: String? = null) {
        viewModelScope.launch {
            showLoading(true)
            checkAccessTokenValidity { accessToken, accessTokenExpirationTime ->
                updateAccessToken(accessToken, accessTokenExpirationTime)
                kycRepository.getKycLastFinalStatus(accessToken).onSuccess { status ->
                    status?.let {
                        showKycStatusScreen(it, statusDescription)
                    }
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

            kycResponse == SoraCardCommonVerification.Failed -> {
                mainRouter.openVerificationFailed(additionalDescription = statusDescription)
            }

            kycResponse == SoraCardCommonVerification.Rejected -> {
                mainRouter.openVerificationRejected(
                    additionalDescription = statusDescription
                )
            }
        }
    }

    fun onKycFailed(statusDescription: String?) {
        viewModelScope.launch {
            mainRouter.openVerificationFailed(additionalDescription = statusDescription)
        }
    }
}
