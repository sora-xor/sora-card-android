package jp.co.soramitsu.oauth.feature.kyc.result

import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.feature.KycCallback
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class VerificationFailedViewModel @Inject constructor(
) : BaseViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_failed_title,
                visibility = true,
                navIcon = null,
            ),
        )
    }

    private var kycCallback: KycCallback? = null

    fun setArgs(kycCallback: KycCallback) {
        this.kycCallback = kycCallback
    }

    fun onClose() {
        kycCallback?.onFinish(
            result = SoraCardResult.Failure(status = SoraCardCommonVerification.Failed)
        )
    }
}
