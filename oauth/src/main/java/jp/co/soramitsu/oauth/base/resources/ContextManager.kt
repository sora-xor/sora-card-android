package jp.co.soramitsu.oauth.base.resources

import android.annotation.SuppressLint
import android.content.Context
import java.util.Locale

@SuppressLint("StaticFieldLeak")
object ContextManager {

    lateinit var context: Context

    fun setBaseContext(context: Context): Context {
        this.context = context
        return context
    }

    fun setLocale(locale: Locale) {
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val newContext = context.createConfigurationContext(configuration)
        this.context = newContext
    }

    fun getLocale(): Locale = Locale.getDefault()
}
