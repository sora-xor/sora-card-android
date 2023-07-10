package jp.co.soramitsu.oauth.feature.verification.successful

import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class VerificationSuccessfulViewModel @Inject constructor(
    private val verificationFlow: VerificationFlow
) : DisposableViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_successful_title,
                visibility = true,
                navIcon = R.drawable.ic_cross
            ),
        )
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        verificationFlow.onExit()
    }

    fun onClose() {
        verificationFlow.onExit()
    }
}
