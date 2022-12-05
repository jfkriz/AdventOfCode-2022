import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
        testLogging.events = mutableSetOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
        exclude("**/dayNN/**")
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
