package github.nwn.kotlin.localization

open class LocaleValueProviderConfigurationScope(protected val baseKey: String, protected val provider: LocaleValueProvider) {
    companion object {
        private const val IMPLICIT_KEY_PREFIX = "implicit_"
    }
    private var implicitEntryCount: Int = 0
    open fun quantity(
        other: LocalizedValue<String>,
        zero: LocalizedValue<String>? = null,
        one: LocalizedValue<String>? = null,
        two: LocalizedValue<String>? = null,
        few: LocalizedValue<String>? = null,
        many: LocalizedValue<String>? = null,
    ) : LocaleValueReference<String> {
        val implicitKey = "$baseKey.${IMPLICIT_KEY_PREFIX}_${implicitEntryCount++}"
        val quantity = LocaleValueReference(implicitKey,provider,QuantityString(other, zero, one, two, few, many))
        provider.references[implicitKey] = quantity
        return quantity
    }
    open fun <T : Any> value(value: T) : LocaleValueReference<T> {
        val implicitKey = "$baseKey.${IMPLICIT_KEY_PREFIX}_${implicitEntryCount++}"
        val v = LocaleValueReference(implicitKey,provider,SimpleLocalizedValue(value))
        provider.references[implicitKey] = v
        return v
    }

}