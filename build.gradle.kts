plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"

}

group = "org.damascus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("io.insert-koin:koin-core:4.0.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")

    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

kover {
    htmlReport {
        onCheck.set(true)

        verify {
            rule {
                isEnabled = true
                name = "Line Coverage of Tests must be more than 80%"
                bound {
                    minValue = 80
                    counter = kotlinx.kover.api.CounterType.LINE
                }
            }
            rule {
                isEnabled = true
                name = "Branch Coverage of Tests must be more than 80%"
                bound {
                    minValue = 80
                    counter = kotlinx.kover.api.CounterType.BRANCH
                }
            }
            rule {
                isEnabled = true
                name = "Instruction Coverage of Tests must be more than 80%"
                bound {
                    minValue = 80
                    counter = kotlinx.kover.api.CounterType.INSTRUCTION
                }
            }
        }
    }
}


kotlin {
    jvmToolchain(17)
}