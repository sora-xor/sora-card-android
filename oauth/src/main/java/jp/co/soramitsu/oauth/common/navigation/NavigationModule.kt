package jp.co.soramitsu.oauth.common.navigation

import dagger.Binds
import dagger.Module
import jp.co.soramitsu.oauth.common.navigation.coordinator.api.NavigationCoordinator
import jp.co.soramitsu.oauth.common.navigation.coordinator.impl.NavigationCoordinatorImpl
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.flow.login.impl.LoginFlowImpl
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.registration.impl.RegistrationFlowImpl
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.verification.impl.VerificationFlowImpl

@Module
interface NavigationModule {

    @Binds
    fun bindNavigationCoordinator(
        navigationCoordinatorImpl: NavigationCoordinatorImpl
    ): NavigationCoordinator

    @Binds
    fun bindLoginFlow(
        loginFlowImpl: LoginFlowImpl
    ): LoginFlow

    @Binds
    fun bindRegistrationFlow(
        registrationFlowImpl: RegistrationFlowImpl
    ): RegistrationFlow

    @Binds
    fun bindVerificationFlow(
        verificationFlowImpl: VerificationFlowImpl
    ): VerificationFlow

}