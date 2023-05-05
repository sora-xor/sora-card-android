package jp.co.soramitsu.oauth.base.extension

import android.annotation.SuppressLint
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import jp.co.soramitsu.oauth.BuildConfig

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.testTagAsId(tag: String): Modifier {
    return if (BuildConfig.DEBUG) {
        this
            .semantics {
                testTagsAsResourceId = true
            }
            .testTag("jp.co.soramitsu.oauth:id/$tag")
    } else {
        this
    }
}