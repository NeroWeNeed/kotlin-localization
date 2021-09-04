plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("kapt") version "1.5.30"
}

group = "org.NWN"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/${properties["githubUsername"]}/kotlin-graph")
        credentials {
            username = project.findProperty("githubUsername").toString()
            password = project.findProperty("githubToken").toString()
        }
    }
}

dependencies {
    //implementation(kotlin("stdlib"))
    implementation(project(":core"))
    compileOnly(project(":annotations"))
    kapt(project(":processor"))
}
kapt {
    arguments {
        arg("github.nwn.kotlin.locale.providers","$buildDir/locales")
    }
}
class LocalizationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        project.task("buildLocaleSchema") {
            dependsOn("build")
            group = "build"
            doLast {

            }
        }
    }

}