package jp.co.soramitsu.oauth.common.navigation.flow.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.impl.kycrequiremetsunfulflled.KycRequirementsUnfulfilledFlowImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KycRequirementsUnfulfilledFlow

@Module
@InstallIn(SingletonComponent::class)
interface FlowModule {

    @Binds
    @Singleton
    @KycRequirementsUnfulfilledFlow
    fun bindKycRequirementUnfulfilledFlow(
        kycRequirementsUnfulfilledFlow: KycRequirementsUnfulfilledFlowImpl,
    ): NavigationFlow
}
