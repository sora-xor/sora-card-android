package jp.co.soramitsu.oauth.feature.verification.getprepared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserOperationResult
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.feature.verification.getprepared.model.GetPreparedState
import jp.co.soramitsu.oauth.feature.verification.getprepared.model.Step
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPreparedViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val verificationFlow: VerificationFlow
) : BaseViewModel() {

    var state by mutableStateOf(GetPreparedState())

    init {
        userInteractor.resultFlow.onEach { result ->
            when(result) {
                is UserOperationResult.ContractData -> {
                    verificationFlow.onLaunchKycContract(
                        kycUserData = result.kycUserData,
                        userCredentials = result.userCredentials,
                        kycReferenceNumber = result.kycReferenceNumber
                    )
                }
                is UserOperationResult.Idle -> { /* DO NOTHING */ }
                is UserOperationResult.Error -> {
                    // TODO add snackbar?
                }
            }
        }.launchIn(viewModelScope)

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.get_prepared_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        state = GetPreparedState(
            steps = listOf(
                Step(
                    index = 1,
                    title = R.string.get_prepared_submit_id_photo_title,
                    description = R.string.get_prepared_submit_id_photo_description
                ),
                Step(
                    index = 2,
                    title = R.string.get_prepared_take_selfie_title,
                    description = R.string.get_prepared_take_selfie_description
                ),
                Step(
                    index = 3,
                    title = R.string.get_prepared_proof_address_title,
                    description = R.string.get_prepared_proof_address_description
                ),
                Step(
                    index = 4,
                    title = R.string.get_prepared_personal_info_title,
                    description = R.string.get_prepared_personal_info_description
                ),
            )
        )
    }

    override fun onToolbarNavigation() {
        verificationFlow.onBack()
    }

    fun onConfirm() = viewModelScope.launch {
        userInteractor.getUserData()
    }
}