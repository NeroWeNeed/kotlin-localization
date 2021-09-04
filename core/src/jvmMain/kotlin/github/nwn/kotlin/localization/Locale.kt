@file:JvmName("JVMLocale")

package github.nwn.kotlin.localization


private var CURRENT_LOCALE = ThreadLocal<Locale>().apply {
    this.set(EmptyLocale)
}
actual var Locale.Companion.current: Locale
    get() = CURRENT_LOCALE.get()
    internal set(value) {
        CURRENT_LOCALE.set(value)
    }
