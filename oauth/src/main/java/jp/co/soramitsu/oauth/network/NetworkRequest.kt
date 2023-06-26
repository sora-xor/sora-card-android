package jp.co.soramitsu.oauth.network

enum class NetworkRequest(val url: String) {

    GET_REFERENCE_NUMBER("get-reference-number"),
    GET_KYC_STATUS("kyc-status"),
    GET_KYC_ATTEMPT_COUNT("kyc-attempt-count")
}
