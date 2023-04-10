package jp.co.soramitsu.oauth.feature.get.prepared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.get.prepared.model.GetPreparedState
import jp.co.soramitsu.oauth.feature.get.prepared.model.Step
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class GetPreparedViewModel @Inject constructor(
    private val mainRouter: MainRouter
) : BaseViewModel() {

    var state by mutableStateOf(GetPreparedState())
        private set

    private var authCallback: OAuthCallback? = null

    init {
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

    fun setArgs(authCallback: OAuthCallback) {
        this.authCallback = authCallback
    }

    fun onConfirm() {
        authCallback?.onStartKyc()
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}