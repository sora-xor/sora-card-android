package jp.co.soramitsu.oauth.common.interactors.account.api

import jp.co.soramitsu.oauth.base.compose.Text

sealed interface AccountOperationResult {

    object Executed: AccountOperationResult

    @JvmInline
    value class Error(
        val text: Text
    ): AccountOperationResult

}