package github.nwn.kotlin.localization



interface LocalizedValue<T : Any> {
    fun get(locale: Locale,provider: LocaleValueProvider): T
    fun get(locale: Locale,provider: LocaleValueProvider, vararg args: Any?): T
}

data class SimpleLocalizedValue<T : Any>(val value: T) : LocalizedValue<T> {
    override fun get(locale: Locale,provider: LocaleValueProvider): T = value

    override fun get(locale: Locale,provider: LocaleValueProvider, vararg args: Any?): T = value

}