package jp.co.soramitsu.oauth.common.navigation.flow.api.destinations

sealed class NavigationFlowDestination(val destination: String) {
    override fun toString(): String = destination
}