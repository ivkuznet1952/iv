import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    application
    alias(libs.plugins.vaadin)
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    // Vaadin 24 requires JDK 17+
    compilerOptions.jvmTarget = JvmTarget.JVM_21
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the stacktraces of failed tests in the CI console.
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    // Vaadin-related dependencies
    implementation(libs.vaadin.core) {
        if (vaadin.effective.productionMode.get()) {
            exclude(module = "vaadin-dev")
        }
    }
    implementation(libs.vok.db)
    implementation(libs.vaadin.security.simple)
    implementation(libs.vaadin.boot)

    // logging
    implementation(libs.slf4j.simple)

    //implementation(kotlin("stdlib-jdk8"))

    // db
    implementation(libs.hikaricp)
    implementation(libs.flyway)
    implementation(libs.flywaydb)
    implementation(libs.postgresql)

    // test support
//    testImplementation(libs.karibu.testing)
//    testImplementation(libs.junit)
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//    testImplementation(kotlin("test"))
    implementation("org.telegram:telegrambots:6.9.7.1")

}

java {
    //
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass = "com.iv.piter.MainKt"
}
