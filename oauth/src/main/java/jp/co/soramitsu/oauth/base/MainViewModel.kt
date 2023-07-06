package jp.co.soramitsu.oauth.base

import androidx.lifecycle.viewModelScope
import com.paywings.onboarding.kyc.android.sdk.util.PayWingsOnboardingKycResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.theme.views.state.DialogAlertState
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.api.models.KycStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val userSessionRepository: UserSessionRepository,
    val inMemoryRepo: InMemoryRepo
) : BaseViewModel() {

    init {
        with(accountInteractor) {
            viewModelScope.launch {
                checkKycVerificationStatus()
            }

            resultFlow.onEach {
                println("This is checkpoint: error - $it")
                dialogState = DialogAlertState(
                    title = it.text,
                    message = it.text,
                    dismissAvailable = true,
                    onPositive = {
                        dialogState = null
                    }
                )
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
}
