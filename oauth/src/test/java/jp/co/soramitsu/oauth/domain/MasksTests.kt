package jp.co.soramitsu.oauth.domain

import androidx.compose.ui.text.AnnotatedString
import jp.co.soramitsu.oauth.base.compose.maskFilter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MasksTests {

    @Test
    fun test01() {
        val phone = "123456789"
        val masked = maskFilter(AnnotatedString(phone))
        assertEquals("+123456789", masked.text.toString())
    }
}
