package jp.co.soramitsu.oauth.common.interactors.prices.impl

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.core.datasources.tachi.api.KycRepository
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.prices.api.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.interactors.prices.api.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import javax.inject.Inject

class PriceInteractorImpl @Inject constructor(
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