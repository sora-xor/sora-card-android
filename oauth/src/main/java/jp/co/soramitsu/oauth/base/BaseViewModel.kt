package jp.co.soramitsu.oauth.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.oauth.theme.views.state.DialogAlertState
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import jp.co.soramitsu.ui_core.component.toolbar.Action
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState

open class BaseViewModel : ViewModel() {

    protected val _toolbarState = MutableLiveData<SoramitsuToolbarState>()
    val toolbarState: LiveData<SoramitsuToolbarState> = _toolbarState

    var dialogState by mutableStateOf<DialogAlertState?>(null)
        protected set

    open fun onToolbarAction() = Unit

    open fun onToolbarNavigation() = Unit

    open fun onToolbarMenuItemSelected(action: Action) = Unit

    private fun onError(throwable: Throwable) {
        if (throwable is RestException) {
            // TODO implement error handling
        }
    }

    suspend fun tryCatch(block: suspend () -> Unit) {
        try {
            block.invoke()
        } catch (t: Throwable) {
            onError(t)
        }
    }
}