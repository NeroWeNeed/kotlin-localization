package github.nwn.kotlin.localization

class LocaleModelTests {
    object LocaleModel : LocaleValueProvider() {
        val choice by choice<String> {
            item("success",true) {
                default = true
                condition {
                    it[0] is Number
                }
                value {
                    quantity("book")
                }
            }
        }
    }
}