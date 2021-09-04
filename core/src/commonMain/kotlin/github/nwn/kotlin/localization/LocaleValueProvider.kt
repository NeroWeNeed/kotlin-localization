package github.nwn.kotlin.localization

import kotlin.reflect.KProperty

abstract class LocaleValueProvider {
    internal val references = HashMap<String, LocaleValueReference<*>>()
    operator fun get(key: String) = references[key]
    fun getValue(key: String) = references.getValue(key)
init {

}
}

inline fun <reified T : Any> LocaleValueProvider.value(defaultValue: T) =
    LocaleValueDelegateFactory<LocaleValueProvider, T>(SimpleLocalizedValue(defaultValue))

fun LocaleValueProvider.span(op: SpanStringScope.() -> String) =
    LocaleSpanDelegateFactory<LocaleValueProvider>(op)
fun <T : Any>  LocaleValueProvider.choice(op: ChoiceValueBuilder<T>.() -> Unit) =
    LocaleChoiceDelegateFactory<LocaleValueProvider,T>(op)


class LocaleValueDelegateFactory<Source : LocaleValueProvider, Value : Any>(private val defaultValue: LocalizedValue<Value>) {
    operator fun provideDelegate(
        thisRef: Source,
        prop: KProperty<*>
    ): LocaleValueDelegate<Source, Value> {
        val reference = LocaleValueReference(
            "${thisRef::class.qualifiedName}.${prop.name}",
            thisRef,
            defaultValue
        )
        thisRef.references[reference.key] = reference

        return LocaleValueDelegate(
            reference
        )
    }
}
class LocaleSpanDelegateFactory<Source : LocaleValueProvider>(private val op: SpanStringScope.() -> String) {
    operator fun provideDelegate(
        thisRef: Source,
        prop: KProperty<*>
    ): LocaleValueDelegate<Source, String> {
        val key = "${thisRef::class.qualifiedName}.${prop.name}"
        val scope = SpanStringScope(key,thisRef)

        val reference = LocaleValueReference(
            key,
            thisRef,
            SpanString.Parser.parse(scope.op())
        )
        thisRef.references[reference.key] = reference

        return LocaleValueDelegate(
            reference
        )
    }
}
class LocaleChoiceDelegateFactory<Source : LocaleValueProvider, Value : Any>(private val op: ChoiceValueBuilder<Value>.() -> Unit) {
    operator fun provideDelegate(
        thisRef: Source,
        prop: KProperty<*>
    ): LocaleValueDelegate<Source, Value> {
        val key = "${thisRef::class.qualifiedName}.${prop.name}"
        val scope = ChoiceValueBuilder<Value>(key,thisRef)
        val reference = LocaleValueReference(
            key,
            thisRef,
            scope.apply(op).build()
        )
        thisRef.references[reference.key] = reference
        return LocaleValueDelegate(
            reference
        )
    }
}

