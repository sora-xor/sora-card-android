package jp.co.soramitsu.oauth.feature

import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult

interface KycCallback {

    fun onFinish(result: SoraCardResult)
}
