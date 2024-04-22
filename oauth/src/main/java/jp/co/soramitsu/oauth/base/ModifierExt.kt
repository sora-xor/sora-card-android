package jp.co.soramitsu.oauth.base

import androidx.compose.ui.Modifier
import jp.co.soramitsu.androidfoundation.compose.testTagAsId
import jp.co.soramitsu.oauth.BuildConfig

fun Modifier.testTagAsId(tag: String): Modifier {
    return if (BuildConfig.DEBUG) {
        this.testTagAsId(main = "jp.co.soramitsu.oauth:id", tag = tag)
    } else {
        this
    }
}
