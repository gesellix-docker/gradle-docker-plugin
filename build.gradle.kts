import java.text.SimpleDateFormat
import java.util.*

rootProject.extra.set("artifactVersion", SimpleDateFormat("yyyy-MM-dd\'T\'HH-mm-ss").format(Date()))

buildscript {
    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.20.0"
    id("net.ossindex.audit") version "0.3.21"
    id("com.jfrog.bintray") version "1.8.4" apply false
    id("com.gradle.plugin-publish") version "0.10.0" apply false
}

val dependencyVersions = listOf(
        "com.squareup.okio:okio:2.1.0",
        "org.jetbrains.kotlin:kotlin-reflect:1.3.11",
        "org.jetbrains.kotlin:kotlin-stdlib:1.3.11",
        "org.jetbrains.kotlin:kotlin-stdlib-common:1.3.11"
)

val dependencyVersionsByGroup = mapOf(
        "org.codehaus.groovy" to "2.5.4"
)

subprojects {
    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()
            force(dependencyVersions)
            eachDependency({
                val forcedVersion = dependencyVersionsByGroup[requested.group]
                if (forcedVersion != null) {
                    useVersion(forcedVersion)
                }
            })
        }
    }
}

tasks {
    register<Wrapper>("updateWrapper") {
        gradleVersion = "5.0"
        distributionType = Wrapper.DistributionType.ALL
    }
}
