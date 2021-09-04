package github.nwn.kotlin.localization

import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


object EmptyLocale : Locale {
    override val identifier: String = ""
    override fun <T : Any> get(reference: LocaleValueReference<T>): T =
        reference.defaultValue.get(this, reference.source)

    override fun <T : Any> get(reference: LocaleValueReference<T>, vararg args: Any?): T =
        reference.defaultValue.get(this, reference.source, args)

    override fun format(input: String, formatType: String, formatStyle: String): String = input
    override fun quantity(input: Number): QuantityString.Quantity = QuantityString.Quantity.Other
}

interface Locale {
    companion object {
        val default = EmptyLocale

    }

    val identifier: String
    operator fun <T : Any> get(reference: LocaleValueReference<T>): T

    fun <T : Any> get(reference: LocaleValueReference<T>, vararg args: Any?): T

    fun format(input: String, formatType: String, formatStyle: String): String
    fun quantity(input: Number): QuantityString.Quantity
}

expect var Locale.Companion.current: Locale
    internal set


data class LocaleValueReference<T : Any>(
    val key: String,
    val source: LocaleValueProvider,
    val defaultValue: LocalizedValue<T>
)

class LocaleValueDelegate<Source : LocaleValueProvider, Value : Any>(private val reference: LocaleValueReference<Value>) :
    ReadOnlyProperty<Source, LocaleValueReference<Value>> {


    override fun getValue(thisRef: Source, property: KProperty<*>): LocaleValueReference<Value> {
        return reference
    }
}

fun localized(value: Locale, op: Locale.Companion.() -> Unit) {

    op(Locale.Companion)

}