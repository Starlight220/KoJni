plugins {
    id ("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    kotlin("jvm") version "1.4.20"
    `maven-publish`
    id("com.diffplug.spotless") version "5.9.0"
}

repositories {
    maven("https://plugins.gradle.org/m2/")
    jcenter()
    mavenCentral()
    mavenLocal()
}

publishing {
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(gradleKotlinDsl())
    runtimeOnly(kotlin("reflect","1.4.20"))


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation(gradleTestKit())
}

version = "1.0.0"
group = "io.github.starlight"
gradlePlugin {
    plugins {
        val kojni by creating {
            id = "io.github.starlight.KoJni"
            implementationClass = "io.github.starlight.kojni.KoJniPlugin"
            displayName = "KoJni"
        }
    }
}
pluginBundle {
    website = "https://github.com/Starlight220/KoJni"
    vcsUrl = "https://github.com/Starlight220/KoJni"
    description = "A Gradle plugin for generating JNI headers."
    tags = listOf("jni", "kotlin")
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