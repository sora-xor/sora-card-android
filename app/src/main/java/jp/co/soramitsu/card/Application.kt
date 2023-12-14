package jp.co.soramitsu.card

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

@HiltAndroidApp
open class Application : Application()

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSoramitsuNetworkClient(): SoramitsuNetworkClient = SoramitsuNetworkClient(
        timeout = 10000,
        logging = true,
    )
}
