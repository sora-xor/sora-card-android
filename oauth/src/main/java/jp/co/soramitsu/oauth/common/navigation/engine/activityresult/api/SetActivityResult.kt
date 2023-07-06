package jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api

import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult

interface SetActivityResult {

    fun setResult(soraCardResult: SoraCardResult)

}