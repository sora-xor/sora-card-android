package jp.co.soramitsu.oauth.common.navigation.flow.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.impl.kycrequiremetsunfulflled.KycRequirementsUnfulfilledFlowImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FlowModule {

    @Binds
    @Singleton
    @KycRequirementsUnfulfilledFlow
    fun bindKycRequirementUnfulfilledFlow(
        kycRequirementsUnfulfilledFlow: KycRequirementsUnfulfilledFlowImpl
    ): NavigationFlow

}