package jp.co.soramitsu.oauth.common.interactors.prices.api

interface PriceInteractor {

    suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency>

    suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency>

    suspend fun calculateCardIssuancePrice(): Result<Double>

    suspend fun calculateKycAttemptPrice(): Result<Double>

}

