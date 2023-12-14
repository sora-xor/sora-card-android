package jp.co.soramitsu.oauth.feature.verify.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import jp.co.soramitsu.oauth.feature.verify.Timer

@InstallIn(ViewModelComponent::class)
@Module
class VerificationModule {

    @ViewModelScoped
    @Provides
    fun provideTimer(): Timer {
        return Timer()
    }
}
