plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
}

group = "org.damascus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

sourceSets.main {
    kotlin.srcDir("build/generated/ksp/main/kotlin")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("io.insert-koin:koin-core:4.0.3")
    implementation("io.insert-koin:koin-annotations:1.3.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")

    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("*di.*", "*ui.*", "*model.*")
                annotatedBy("*KoverIgnore")
            }
        }

        verify {
            rule {
                bound {
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.INSTRUCTION
                    minValue = 80
                }
            }

            rule {
                bound {
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH
                    minValue = 80
                }
            }

            rule {
                bound {
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                    minValue = 80
                }
            }
        }
    }
}


kotlin {
    jvmToolchain(17)
}