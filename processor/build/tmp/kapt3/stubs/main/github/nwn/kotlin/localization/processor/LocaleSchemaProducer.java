package github.nwn.kotlin.localization.processor;

import java.lang.System;

@com.google.auto.service.AutoService(value = {javax.annotation.processing.Processor.class})
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\t\u001a\u00020\n2\u000e\u0010\u000b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lgithub/nwn/kotlin/localization/processor/LocaleSchemaProducer;", "Ljavax/annotation/processing/AbstractProcessor;", "()V", "classInspector", "Lcom/squareup/kotlinpoet/metadata/specs/ClassInspector;", "init", "", "processingEnv", "Ljavax/annotation/processing/ProcessingEnvironment;", "process", "", "annotations", "", "Ljavax/lang/model/element/TypeElement;", "roundEnv", "Ljavax/annotation/processing/RoundEnvironment;", "Companion", "processor"})
@javax.annotation.processing.SupportedAnnotationTypes(value = {"github.nwn.kotlin.localization.Localization"})
@javax.annotation.processing.SupportedOptions(value = {"kapt.kotlin.generated", "github.nwn.kotlin.locale.providers"})
@javax.annotation.processing.SupportedSourceVersion(value = javax.lang.model.SourceVersion.RELEASE_16)
public final class LocaleSchemaProducer extends javax.annotation.processing.AbstractProcessor {
    @org.jetbrains.annotations.NotNull
    public static final github.nwn.kotlin.localization.processor.LocaleSchemaProducer.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String LOCALE_PROVIDER_LIST_OUTPUT_DIRECTORY = "github.nwn.kotlin.locale.providers";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String LOCALE_PROVIDER_FILE_NAME = "locale_providers.json";
    private com.squareup.kotlinpoet.metadata.specs.ClassInspector classInspector;
    
    public LocaleSchemaProducer() {
        super();
    }
    
    @java.lang.Override
    public void init(@org.jetbrains.annotations.NotNull
    javax.annotation.processing.ProcessingEnvironment processingEnv) {
    }
    
    @java.lang.Override
    public boolean process(@org.jetbrains.annotations.NotNull
    java.util.Set<? extends javax.lang.model.element.TypeElement> annotations, @org.jetbrains.annotations.NotNull
    javax.annotation.processing.RoundEnvironment roundEnv) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lgithub/nwn/kotlin/localization/processor/LocaleSchemaProducer$Companion;", "", "()V", "KAPT_KOTLIN_GENERATED_OPTION_NAME", "", "LOCALE_PROVIDER_FILE_NAME", "LOCALE_PROVIDER_LIST_OUTPUT_DIRECTORY", "processor"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}