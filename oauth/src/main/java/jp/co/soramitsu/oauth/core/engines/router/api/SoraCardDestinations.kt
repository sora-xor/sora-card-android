package jp.co.soramitsu.oauth.core.engines.router.api

interface SoraCardDestinations {

    val route: String

    /* Helper */
    object Loading: SoraCardDestinations {
        override val route: String = "LOADING"
    }

}