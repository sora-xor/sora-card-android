package jp.co.soramitsu.oauth.common.navigation.flow.registration.api

sealed interface RegistrationDestination {

    object EnterFirstAndLastName: RegistrationDestination

    object EnterEmail: RegistrationDestination

    object EmailConfirmation: RegistrationDestination

}