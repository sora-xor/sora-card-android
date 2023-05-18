package jp.co.soramitsu.oauth.common.domain

import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency

interface PriceInteractor {

    suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency>

    suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency>

    suspend fun calculateCardIssuancePrice(): Result<Double>

    suspend fun calculateKycAttemptPrice(): Result<Double>

}

