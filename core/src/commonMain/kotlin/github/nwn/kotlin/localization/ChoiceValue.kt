package github.nwn.kotlin.localization

class ChoiceValue<T : Any>(private val choices: List<Choice> = emptyList(),private val defaultIndex: Int = 0) : LocalizedValue<T> {
    override fun get(locale: Locale, provider: LocaleValueProvider): T {
        return locale[provider.getValue(choices[defaultIndex].key)] as T
    }

    override fun get(locale: Locale, provider: LocaleValueProvider, vararg args: Any?): T {
        val key = choices.firstOrNull() { it(args) }?.key ?: choices[defaultIndex].key
        return locale.get(provider.getValue(key), args) as T
    }

    data class Choice(val key: String, val default: Boolean, val condition: (Array<out Any?>) -> Boolean) {
        operator fun invoke(vararg args: Any?) = condition(args)
    }
}

class ChoiceValueBuilder<T : Any>(private val baseKey: String, private val provider: LocaleValueProvider) {
    companion object {
        private const val IMPLICIT_KEY_PREFIX = "implicit_"
        private const val CHOICE_PREFIX = "choices"
    }

    private val choices = ArrayList<ChoiceValue.Choice>()
    private var defaultSet: Boolean = false
    private var implicitEntryCount: Int = 0
    fun item(
        key: String = "",
        default: Boolean = false,
        condition: (Array<out Any?>) -> Boolean,
        value: ItemValueBuilder.() -> LocaleValueReference<T>
    ) {
        val def = !defaultSet && default
        defaultSet = defaultSet || default
        val choiceKey =
            "${this.baseKey}.$CHOICE_PREFIX.${key.ifBlank { "${IMPLICIT_KEY_PREFIX}_${implicitEntryCount++}" }}"
        val builder = ItemValueBuilder(choiceKey, provider)
        val reference = builder.value()
        provider.references[choiceKey] = reference
        choices.add(ChoiceValue.Choice(choiceKey, def, condition))


    }
    fun item(
        key: String = "",
        default: Boolean = false,
        op: ItemBuilder.() -> Unit
    ) {
        val itemBuilder = ItemBuilder()
        itemBuilder.key = key
        itemBuilder.default = default
        itemBuilder.op()
        //TODO: Ensure ItemBuilder Value is set
        item(key,itemBuilder.default,itemBuilder.condition ?: { false },itemBuilder.value!!)
    }

    internal fun build() = ChoiceValue<T>(choices,if (defaultSet) choices.indexOfFirst { it.default } else 0)

    inner class ItemBuilder() {
        var key: String = ""
        internal var condition: ((Array<out Any?>) -> Boolean)? = null
        internal var value: (ItemValueBuilder.() -> LocaleValueReference<T>)? = null
        var default: Boolean = false



        fun condition(op: (Array<out Any?>) -> Boolean) {
            condition = op
        }

        fun value(op: ItemValueBuilder.() -> LocaleValueReference<T>) {
            value = op
        }
    }

}

class ItemValueBuilder(key: String, provider: LocaleValueProvider) :
    LocaleValueProviderConfigurationScope(key, provider) {


}