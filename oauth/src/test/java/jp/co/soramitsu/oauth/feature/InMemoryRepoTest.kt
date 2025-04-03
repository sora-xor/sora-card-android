package jp.co.soramitsu.oauth.feature

import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.network.NetworkRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class InMemoryRepoTest {

    private lateinit var memo: InMemoryRepo

    @Before
    fun setUp() {
        memo = InMemoryRepo()
    }

    @Test
    fun `test locale`() = runTest {
        assertEquals("en", memo.locale)
        memo.locale = "boo"
        assertEquals("boo", memo.locale)
    }

    @Test
    fun `test env`() = runTest {
        assertEquals(SoraCardEnvironmentType.NOT_DEFINED, memo.environment)
        memo.environment = SoraCardEnvironmentType.PRODUCTION
        assertEquals(SoraCardEnvironmentType.PRODUCTION, memo.environment)

        assertEquals("jp.co.soramitsu.oauth", memo.client)
        memo.client = "pac name"
        assertEquals("pac name", memo.client)

        assertNull(memo.flow)
        memo.flow = SoraCardFlow.SoraCardGateHubFlow
        assertEquals(SoraCardFlow.SoraCardGateHubFlow, memo.flow)

        assertEquals("pac name/null/null/0", memo.networkHeader)

        assertEquals("", memo.soraBackEndUrl)

        memo.ghExpectedExchangeVolume = 23
        assertEquals(23, memo.ghExpectedExchangeVolume)
    }

    @Test
    fun `test url`() = runTest {
        var r = memo.url(null, NetworkRequest.HEALTH)
        assertEquals("health", r)
        r = memo.url("http//", NetworkRequest.HEALTH)
        assertEquals("http//health", r)
        memo.soraBackEndUrl = "backurl:"
        r = memo.url(null, NetworkRequest.FEES)
        assertEquals("backurl:fees", r)
    }
}
