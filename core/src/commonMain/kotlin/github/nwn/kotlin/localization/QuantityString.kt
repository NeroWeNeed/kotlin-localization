package github.nwn.kotlin.localization

import kotlin.jvm.JvmInline

@JvmInline
value class QuantityString(private val quantities: List<LocalizedValue<String>?>) : LocalizedValue<String> {
    constructor(
        other: LocalizedValue<String>,
        zero: LocalizedValue<String>? = null,
        one: LocalizedValue<String>? = null,
        two: LocalizedValue<String>? = null,
        few: LocalizedValue<String>? = null,
        many: LocalizedValue<String>? = null,
    ) : this(listOf(other, zero, one, two, few, many))

    enum class Quantity {
        Other, Zero, One, Two, Few, Many
    }

    override fun get(locale: Locale, provider: LocaleValueProvider): String = quantities[Quantity.Other.ordinal]!!.get(locale, provider)

    override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): String =
        quantities[args[0].let { if (it is Number) locale.quantity(it) else Quantity.Other }.ordinal]?.get(locale, provider, args) ?: get(locale, provider)

}
