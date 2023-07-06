package jp.co.soramitsu.oauth.common.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.common.navigation.coordinator.api.NavigationCoordinator
import jp.co.soramitsu.oauth.common.navigation.coordinator.impl.NavigationCoordinatorImpl
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.common.navigation.flow.login.impl.LoginFlowImpl
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.registration.impl.RegistrationFlowImpl
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.verification.impl.VerificationFlowImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
interface NavigationModule {

    @Binds
    @ActivityRetainedScoped
    fun bindNavigationCoordinator(
        navigationCoordinatorImpl: NavigationCoordinatorImpl
    ): NavigationCoordinator

    @Binds
    @ActivityRetainedScoped
    fun bindLoginFlow(
        loginFlowImpl: LoginFlowImpl
    ): LoginFlow

    @Binds
    @ActivityRetainedScoped
    fun bindRegistrationFlow(
        registrationFlowImpl: RegistrationFlowImpl
    ): RegistrationFlow

    @Binds
    @ActivityRetainedScoped
    fun bindVerificationFlow(
        verificationFlowImpl: VerificationFlowImpl
    ): VerificationFlow

}