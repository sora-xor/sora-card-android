package jp.co.soramitsu.oauth.base.extension

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

fun Fragment.onBackPressed(block: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                block()
            }
        },
    )
}

fun ComponentActivity.onActivityBackPressed(block: () -> Unit) {
    onBackPressedDispatcher.addCallback(
        this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                block()
            }
        },
    )
}
