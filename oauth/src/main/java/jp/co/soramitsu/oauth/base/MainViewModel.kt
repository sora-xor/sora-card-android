package jp.co.soramitsu.oauth.base

import androidx.lifecycle.viewModelScope
import com.paywings.onboarding.kyc.android.sdk.util.PayWingsOnboardingKycResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import jp.co.soramitsu.oauth.theme.views.obtainStringAsAny
import jp.co.soramitsu.oauth.theme.views.state.DialogAlertState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val userSessionRepository: UserSessionRepository,
    val inMemoryRepo: InMemoryRepo
) : DisposableViewModel() {

    private var disposableJob: Job? = null

    init {
        with(accountInteractor) {
            disposableJob = resultFlow.onStart {
                viewModelScope.launch {
                    checkKycVerificationStatus()
                }
            }.onEach { result ->
                when(result) {
                    is AccountOperationResult.Executed -> {
                        /* DO NOTHING */
                    }
                    is AccountOperationResult.Loading -> {
                        /* DO NOTHING */
                    }
                    is AccountOperationResult.Error -> {
                        dialogState = DialogAlertState(
                            message = result.text.obtainStringAsAny(),
                            dismissAvailable = true,
                            onPositive = {
                                dialogState = null
                            }
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setPayWingsKycResult(result: PayWingsOnboardingKycResult) =
        when(result) {
            is PayWingsOnboardingKycResult.Success -> viewModelScope.launch {
                accountInteractor.checkKycVerificationStatus()
            }
            is PayWingsOnboardingKycResult.Failure -> viewModelScope.launch {
                userSessionRepository.setKycStatus(KycStatus.Failed)
            }
        }

    override fun onCleared() {
        super.onCleared()
        disposableJob?.cancel()
    }
}
