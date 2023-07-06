package jp.co.soramitsu.oauth.core.datasources.tachi.impl

enum class NetworkRequest(val url: String) {
    GET_REFERENCE_NUMBER("get-reference-number"),
    GET_KYC_STATUS("kyc-status"),
    GET_KYC_FREE_ATTEMPT_INFO("kyc-attempt-count")
}
