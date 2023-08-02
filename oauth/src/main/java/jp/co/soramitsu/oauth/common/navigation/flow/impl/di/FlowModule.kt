package jp.co.soramitsu.oauth.common.navigation.flow.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.common.navigation.flow.impl.kycrequiremetsunfulflled.KycRequirementsUnfulfilledFlowImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
interface FlowModule {

    @Binds
    @ActivityRetainedScoped
    @KycRequirementsUnfulfilledFlow
    fun bindKycRequirementUnfulfilledFlow(
        kycRequirementsUnfulfilledFlow: KycRequirementsUnfulfilledFlowImpl
    ): NavigationFlow

}