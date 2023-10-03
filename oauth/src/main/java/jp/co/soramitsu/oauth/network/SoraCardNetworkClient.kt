package jp.co.soramitsu.oauth.network

import android.os.Build
import io.ktor.client.statement.HttpResponse
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import kotlin.coroutines.cancellation.CancellationException

class SoraCardNetworkClient(
    private val client: SoramitsuNetworkClient,
    private val inMemoryRepo: InMemoryRepo,
) {

    private val header: String by lazy {
        "${inMemoryRepo.client}/${Build.MANUFACTURER}/${Build.MODEL}/${Build.VERSION.SDK_INT}"
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun post(bearerToken: String?, url: String, body: Any): HttpResponse =
        client.postJsonRequest(
            bearerToken = bearerToken,
            header = header,
            body = body,
            url = inMemoryRepo.soraBackEndUrl + url,
        )

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun get(bearerToken: String?, url: String, baseUrl: String? = null): HttpResponse =
        client.getJsonRequest(
            bearerToken = bearerToken,
            header = header,
            url = (baseUrl ?: inMemoryRepo.soraBackEndUrl) + url,
        )
}
