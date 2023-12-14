package jp.co.soramitsu.oauth.feature.terms.and.conditions.model

enum class WebUrl(val url: String) {
    GENERAL_TERMS("https://soracard.com/terms/"),
    PRIVACY_POLICY("https://soracard.com/privacy/"),
    ;

    fun asArgument(): String {
        return "/$name"
    }
}
