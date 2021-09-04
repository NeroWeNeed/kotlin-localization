package github.nwn.kotlin.localization.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.isDelegated
import com.squareup.kotlinpoet.metadata.isDelegation
import com.squareup.kotlinpoet.metadata.specs.ClassInspector
import com.squareup.kotlinpoet.metadata.specs.classFor
import com.squareup.kotlinpoet.metadata.specs.internal.ClassInspectorUtil
import com.squareup.kotlinpoet.typeNameOf
import github.nwn.kotlin.localization.Localization
import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.JvmFieldSignature
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_16)
@SupportedOptions(
    LocaleSchemaProducer.KAPT_KOTLIN_GENERATED_OPTION_NAME,
    LocaleSchemaProducer.LOCALE_PROVIDER_LIST_OUTPUT_DIRECTORY
)
@AutoService(Processor::class)
@SupportedAnnotationTypes("github.nwn.kotlin.localization.Localization")
class LocaleSchemaProducer : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val LOCALE_PROVIDER_LIST_OUTPUT_DIRECTORY = "github.nwn.kotlin.locale.providers"
        const val LOCALE_PROVIDER_FILE_NAME = "locale_providers.json"
    }
    //private lateinit var classInspector: ClassInspector

/*    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        classInspector = ElementsClassInspector.create(processingEnv.elementUtils,processingEnv.typeUtils)
    }*/

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val outputDirectory = processingEnv.options[LOCALE_PROVIDER_LIST_OUTPUT_DIRECTORY].orEmpty().ifEmpty {
            error("Missing output directory for locale providers")
        }.let { File(it) }

        val names = roundEnv.getElementsAnnotatedWith(Localization::class.java).mapNotNull {




            if (it is QualifiedNameable) {
                it.qualifiedName.toString()
            }
            else
                null
        }
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, names.toString())
        with(File(outputDirectory, LOCALE_PROVIDER_FILE_NAME)) {
            parentFile?.mkdirs()
            if (exists()) {
                try {
                    writeText(Json.encodeToString(Json.decodeFromString<List<String>>(readText()).toMutableSet().apply {
                        addAll(names)
                    }))
                } catch (exception: SerializationException) {
                    writeText(Json.encodeToString(names))
                }
            }
            else {
                if (createNewFile()) {
                    writeText(Json.encodeToString(names))
                } else {
                    error("Unable to create $this")
                }
            }

        }

        return true
    }


}