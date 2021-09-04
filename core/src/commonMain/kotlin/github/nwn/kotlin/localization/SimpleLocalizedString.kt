package github.nwn.kotlin.localization

import kotlin.jvm.JvmInline

@JvmInline
value class SimpleLocalizedString(val value: String) : LocalizedValue<String> {
    override fun get(locale: Locale,provider: LocaleValueProvider): String = value

    override fun get(locale: Locale,provider: LocaleValueProvider, vararg args: Any?): String = formatString(value, args)

}

expect fun formatString(str: String, vararg args: Any) : String