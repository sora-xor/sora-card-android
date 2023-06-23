package jp.co.soramitsu.oauth.core.datasources

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.core.datasources.paywings.api.PayWingsRepository
import jp.co.soramitsu.oauth.core.datasources.paywings.impl.PayWingsRepositoryImpl
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.core.datasources.session.impl.UserSessionRepositoryImpl
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import jp.co.soramitsu.oauth.core.datasources.tachi.impl.KycRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourcesModule {

    @Binds
    @Singleton
    fun bindPayWingsRepository(payWingsRepositoryImpl: PayWingsRepositoryImpl): PayWingsRepository

    @Binds
    @Singleton
    fun bindUserSessionRepository(
        userSessionRepositoryImpl: UserSessionRepositoryImpl
    ): UserSessionRepository

    @Binds
    @Singleton
    fun bindKycRepository(kycRepositoryImpl: KycRepositoryImpl): KycRepository

}