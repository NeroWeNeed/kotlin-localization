import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.5.21"
}

group = "org.example"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/${properties["githubUsername"]}/kotlin-graph")
    }
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    jvm() {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation(kotlin("stdlib-common"))

                compileOnly(project(":annotations"))
                implementation("github.nwn:kotlin-graph:0.1-SNAPSHOT")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        all {
            languageSettings {
                languageVersion = "1.5"
            }
        }
    }

}
tasks.named<Test>("test") {
    useJUnitPlatform()
}
