package jp.co.soramitsu.oauth.feature.getprepared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetPreparedViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
) : BaseViewModel() {

    private val _state = MutableStateFlow(GetPreparedState(totalFreeAttemptsCount = ""))
    val state = _state.asStateFlow()

    private var authCallback: OAuthCallback? = null

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.get_prepared_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
                actionLabel = R.string.log_out,
            ),
        )

        _state.value = GetPreparedState(
            totalFreeAttemptsCount = ".",
            steps = listOf(
                Step(
                    index = 1,
                    title = R.string.get_prepared_submit_id_photo_title,
                    description = listOf(R.string.get_prepared_submit_id_photo_description),
                ),
                Step(
                    index = 2,
                    title = R.string.get_prepared_take_selfie_title,
                    description = listOf(R.string.get_prepared_take_selfie_description),
                ),
                Step(
                    index = 3,
                    title = R.string.get_prepared_proof_address_title,
                    description = listOf(R.string.get_prepared_proof_address_description, R.string.get_prepared_proof_address_note),
                ),
                Step(
                    index = 4,
                    title = R.string.get_prepared_personal_info_title,
                    description = listOf(R.string.get_prepared_personal_info_description),
                ),
            )
        )
        viewModelScope.launch {
            val token = userSessionRepository.getAccessToken()
            kycRepository.getFreeKycAttemptsInfo(token)
                .onSuccess {
                    _state.value = _state.value.copy(
                        totalFreeAttemptsCount = it.totalFreeAttemptsCount.toString(),
                    )
                }
        }
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        runCatching {
            viewModelScope.launch {
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                setActivityResult.setResult(SoraCardResult.Logout)
            }
        }
    }

    fun setArgs(authCallback: OAuthCallback) {
        this.authCallback = authCallback
    }

    fun onConfirm() {
        authCallback?.onStartKyc()
    }

    override fun onToolbarNavigation() {
        setActivityResult.setResult(SoraCardResult.Canceled)
    }
}