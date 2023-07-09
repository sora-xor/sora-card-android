package jp.co.soramitsu.oauth.feature.verification.failed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class VerificationFailedViewModel @Inject constructor(
    private val verificationFlow: VerificationFlow
) : BaseViewModel() {

    var additionalInfo by mutableStateOf("")
        private set

    init {
        verificationFlow.args[VerificationDestination.VerificationFailed::class.java.name]
            .apply {
                if (this == null)
                    return@apply

                additionalInfo = getString(
                    VerificationDestination.VerificationFailed.ADDITIONAL_INFO_KEY,
                    ""
                )
            }

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_failed_title,
                visibility = true,
                navIcon = null,
            ),
        )
    }

    fun onClose() {
        verificationFlow.onExit()
    }
}
