@file:JvmName("JVMSimpleLocalizedString")
package github.nwn.kotlin.localization

actual fun formatString(str: String,vararg args: Any) : String = str.format(args)