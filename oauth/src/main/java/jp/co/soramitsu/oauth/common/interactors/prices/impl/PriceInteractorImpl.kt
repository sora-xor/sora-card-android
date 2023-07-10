package jp.co.soramitsu.oauth.common.interactors.prices.impl

import android.os.Build
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.core.datasources.tachi.api.TachiRepository
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.prices.api.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.interactors.prices.api.XorLiquiditySufficiency
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import java.util.StringJoiner
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PriceInteractorImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val inMemoryRepo: InMemoryRepo,
    private val tachiRepository: TachiRepository
): PriceInteractor {

    private val header by lazy {
        StringJoiner(HEADER_DELIMITER).apply {
            add(inMemoryRepo.client)
            add(Build.MANUFACTURER)
            add(Build.VERSION.SDK_INT.toString())
        }.toString()
    }

    override suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency> {
        val (accessToken, accessTokenExpirationTime) = userSessionRepository.run {
            getAccessToken() to getAccessTokenExpirationTime()
        }

        if (accessToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis()))
            return Result.failure(RuntimeException(ACCESS_TOKEN_EXPIRED))

        return tachiRepository.getCurrentXorEuroPrice(header, accessToken)
            .map {
                val xorLiquidityFullPrice =
                    inMemoryRepo.euroLiquidityThreshold.div(it.price)

                XorLiquiditySufficiency(
                    xorInsufficiency = xorLiquidityFullPrice - inMemoryRepo.userAvailableXorAmount,
                    xorLiquidityFullPrice = xorLiquidityFullPrice
                )
            }
    }


    override suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency> {
        val (accessToken, accessTokenExpirationTime) = userSessionRepository.run {
            getAccessToken() to getAccessTokenExpirationTime()
        }

        if (accessToken.isBlank() ||
            accessTokenExpirationTime <= TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis()))
            return Result.failure(RuntimeException(ACCESS_TOKEN_EXPIRED))

        return tachiRepository.getCurrentXorEuroPrice(header, accessToken)
            .map {
                val userAvailableEuroAmount =
                    inMemoryRepo.userAvailableXorAmount.times(it.price)

                EuroLiquiditySufficiency(
                    euroInsufficiency = inMemoryRepo.euroLiquidityThreshold - userAvailableEuroAmount,
                    euroLiquidityFullPrice = inMemoryRepo.euroLiquidityThreshold.toDouble()
                )
            }
    }

    override suspend fun calculateCardIssuancePrice(): Result<Double> =
        kotlin.runCatching { inMemoryRepo.euroCardIssuancePrice.toDouble() }

    override suspend fun calculateKycAttemptPrice(): Result<Double> =
        kotlin.runCatching { inMemoryRepo.kycAttemptPrice }

    private companion object {
        const val HEADER_DELIMITER = "/"

        const val ACCESS_TOKEN_EXPIRED =
            "Access token has been expired, be sure to retrieve new one before proceeding"
    }
}