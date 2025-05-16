plugins {
    kotlin("jvm") version "2.1.20"
    id("jacoco")
}

group = "org.damascus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // MongoDB Kotlin driver
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.5.0")

    // Mongo Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("org.slf4j:slf4j-nop:2.0.9")

    // Kotlin reflect
    implementation(kotlin("reflect"))

    // Kotlinx datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

    // Koin di
    implementation("io.insert-koin:koin-core:4.0.3")

    // Environment values
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:mongodb:1.18.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(
                "**/model/**",
                "**/di/**",
                "**/dto/**",
                "**/mapper/**",
                "**/ui/**",
                "**/MainKt.class"
            )
        }
    )

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.8".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.8".toBigDecimal()
            }
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.13"
}

kotlin {
    jvmToolchain(17)
}