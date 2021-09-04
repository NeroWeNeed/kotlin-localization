plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("kapt") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":annotations"))
    implementation("com.google.auto.service:auto-service:+")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet
    implementation("com.squareup:kotlinpoet:1.9.0")
    implementation("com.squareup:kotlinpoet-metadata:1.9.0")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.9.0")
    implementation("com.squareup:kotlinpoet-classinspector-elements:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.3.0")



    kapt("com.google.auto.service:auto-service:+")
}
kotlin {
    sourceSets {
        all {
            languageSettings {
                useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
                useExperimentalAnnotation("com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview")
            }
        }
    }
}