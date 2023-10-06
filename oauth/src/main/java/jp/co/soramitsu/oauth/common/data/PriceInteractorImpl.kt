package jp.co.soramitsu.oauth.common.data

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository

class PriceInteractorImpl(
    private val userSessionRepository: UserSessionRepository,
    private val inMemoryCache: InMemoryRepo,
    private val kycRepository: KycRepository,
) : PriceInteractor {

    override suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency> {
        val accessToken = userSessionRepository.getAccessToken()

        return kycRepository.getCurrentXorEuroPrice(accessToken)
            .map {
                val xorLiquidityFullPrice =
                    inMemoryCache.euroLiquidityThreshold.div(it)

                XorLiquiditySufficiency(
                    xorInsufficiency = xorLiquidityFullPrice - inMemoryCache.userAvailableXorAmount,
                    xorLiquidityFullPrice = xorLiquidityFullPrice,
                )
            }
    }

    override suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency> {
        val accessToken = userSessionRepository.getAccessToken()

        return kycRepository.getCurrentXorEuroPrice(accessToken)
            .map {
                val userAvailableEuroAmount =
                    inMemoryCache.userAvailableXorAmount.times(it)

                EuroLiquiditySufficiency(
                    euroInsufficiency = inMemoryCache.euroLiquidityThreshold - userAvailableEuroAmount,
                    euroLiquidityFullPrice = inMemoryCache.euroLiquidityThreshold.toDouble()
                )
            }
    }

    override suspend fun calculateCardIssuancePrice(): String =
        kycRepository.getApplicationFee()

    override suspend fun calculateKycAttemptPrice(): String =
        kycRepository.getRetryFee()
}
