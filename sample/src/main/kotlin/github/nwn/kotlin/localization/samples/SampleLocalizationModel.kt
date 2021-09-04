package github.nwn.kotlin.localization.samples

import github.nwn.kotlin.localization.LocaleValueProvider
import github.nwn.kotlin.localization.Localization
import github.nwn.kotlin.localization.span
import github.nwn.kotlin.localization.value
import java.awt.Color

@Localization
object SampleLocalizationModel : LocaleValueProvider() {
    val sample by value(Color.black)



}