package jp.co.soramitsu.oauth.base.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.ui_core.component.toolbar.Action

enum class ToolbarType {
    NO_TOOLBAR,
    CENTER_ALIGNED,
    SMALL,
    MEDIUM,
    LARGE,
}

data class ToolbarState(
    val type: ToolbarType = ToolbarType.CENTER_ALIGNED,
    val title: String = "",
    val navIcon: Int? = R.drawable.ic_toolbar_back,
    val action: String? = null,
    val menuActions: List<Action>? = null,
)
