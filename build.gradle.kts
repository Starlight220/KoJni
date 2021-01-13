plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.4.20"
    id ("com.gradle.plugin-publish") version "0.12.0"
    id("com.diffplug.spotless") version "5.9.0"
}

repositories {
    maven("https://plugins.gradle.org/m2/")
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(gradleKotlinDsl())


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

gradlePlugin {
    plugins {
        create("KoJni") {
            id = "io.github.starlight"
            implementationClass = "io.github.starlight.kojni.KoJniPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

spotless {
    kotlin {
        ktfmt()
        indentWithSpaces(2)
        endWithNewline()
    }
}