package jp.co.soramitsu.oauth.common.data

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import javax.inject.Inject

class PriceInteractorImpl(
    private val userSessionRepository: UserSessionRepository,
    private val inMemoryCache: InMemoryRepo,
    private val kycRepository: KycRepository
): PriceInteractor {

    override suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency> {
        val accessToken = userSessionRepository.getAccessToken()

        return kycRepository.getCurrentXorEuroPrice(accessToken)
            .map {
                val xorLiquidityFullPrice =
                    inMemoryCache.euroLiquidityThreshold.div(it.price)

                XorLiquiditySufficiency(
                    xorInsufficiency = xorLiquidityFullPrice - inMemoryCache.userAvailableXorAmount,
                    xorLiquidityFullPrice = xorLiquidityFullPrice
                )
            }
    }


    override suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency> {
        val accessToken = userSessionRepository.getAccessToken()

        return kycRepository.getCurrentXorEuroPrice(accessToken)
            .map {
                val userAvailableEuroAmount =
                    inMemoryCache.userAvailableXorAmount.times(it.price)

                EuroLiquiditySufficiency(
                    euroInsufficiency = inMemoryCache.euroLiquidityThreshold - userAvailableEuroAmount,
                    euroLiquidityFullPrice = inMemoryCache.euroLiquidityThreshold.toDouble()
                )
            }
    }

    override suspend fun calculateCardIssuancePrice(): Result<Double> =
        kotlin.runCatching { inMemoryCache.euroCardIssuancePrice.toDouble() }

    override suspend fun calculateKycAttemptPrice(): Result<Double> =
        kotlin.runCatching { inMemoryCache.kycAttemptPrice }
}