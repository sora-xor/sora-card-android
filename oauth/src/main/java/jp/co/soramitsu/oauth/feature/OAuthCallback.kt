package jp.co.soramitsu.oauth.feature

interface OAuthCallback {

    fun onOAuthSucceed()

    fun onStartKyc()
}
