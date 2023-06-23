package jp.co.soramitsu.oauth.feature

import androidx.lifecycle.viewModelScope
import com.paywings.onboarding.kyc.android.sdk.util.PayWingsOnboardingKycResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    accountInteractor: AccountInteractor,
    val inMemoryRepo: InMemoryRepo
) : BaseViewModel() {

    init {
        with(accountInteractor) {
            viewModelScope.launch {
                checkEmailVerificationStatus()
            }

            resultFlow.onEach {
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

    fun setPayWingsKycResult(result: PayWingsOnboardingKycResult) {
        TODO()
    }
}
