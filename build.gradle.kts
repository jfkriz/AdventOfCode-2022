import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    kotlin("plugin.serialization") version "1.7.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging.events = mutableSetOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
        filter {
            // Don't run tests on the "template" classes
            excludeTestsMatching("dayNN.*")
            // Don't run tests on real input during CI build, since those will fail (personal input data for problems is not checked in to github)
            if (System.getenv("CI") == "true") {
                excludeTestsMatching("*Real Input*")
            }
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
        java {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    sourceSets["test"].resources {
        srcDirs("src/test/kotlin")
        exclude("**/*.kt")
    }
}
