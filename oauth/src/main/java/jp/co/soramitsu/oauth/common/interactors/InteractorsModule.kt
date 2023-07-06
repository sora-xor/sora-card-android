package jp.co.soramitsu.oauth.common.interactors

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.impl.AccountInteractorImpl
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.prices.impl.PriceInteractorImpl
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.interactors.user.impl.UserInteractorImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
interface InteractorsModule {

    @Binds
    @ActivityRetainedScoped
    fun bindAccountInteractor(
        accountInteractorImpl: AccountInteractorImpl
    ): AccountInteractor

    @Binds
    @ActivityRetainedScoped
    fun bindPriceInteractor(
        priceInteractorImpl: PriceInteractorImpl
    ): PriceInteractor

    @Binds
    @ActivityRetainedScoped
    fun bindUserInteractor(
        userInteractorImpl: UserInteractorImpl
    ): UserInteractor

}