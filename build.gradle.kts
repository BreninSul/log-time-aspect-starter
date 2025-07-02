import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.0.0"
    val springBootVersion = "3.4.2"
    id("java")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
    id("org.jetbrains.dokka") version "1.9.20"
}
val springBootVersion = "3.4.2"
val kotlinVersion = "2.0.0"
val javaVersion = JavaVersion.VERSION_17

group = "io.github.breninsul"
version = "1.0.1"

java {
    sourceCompatibility = javaVersion
}

java {
    withJavadocJar()
    withSourcesJar()
}
repositories {
    mavenCentral()
}
tasks.compileJava {
    dependsOn.add(tasks.processResources)
}
tasks.compileKotlin {
    dependsOn.add(tasks.processResources)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.aspectj:aspectjweaver:1.9.24")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = javaVersion.majorVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

signing {
    useGpgCmd()
}

centralPortal {
    pom {
        packaging = "jar"
        name.set("BreninSul Log Time Aspect Starter")
        val repositoryName = "log-time-aspect-starter"
        url.set("https://github.com/BreninSul/$repositoryName")
        description.set(
            """
A lightweight Spring Boot starter library that provides aspect-oriented logging capabilities for method execution time and error handling.
            """.trimIndent(),
        )
        licenses {
            license {
                name.set("MIT License")
                url.set("http://opensource.org/licenses/MIT")
            }
        }
        scm {
            connection.set("scm:https://github.com/BreninSul/$repositoryName.git")
            developerConnection.set("scm:git@github.com:BreninSul/$repositoryName.git")
            url.set("https://github.com/BreninSul/$repositoryName")
        }
        developers {
            developer {
                id.set("BreninSul")
                name.set("BreninSul")
                email.set("brenimnsul@gmail.com")
                url.set("breninsul.github.io")
            }
        }
    }
}

tasks.jar {
    enabled = true
    archiveClassifier.set("")
}
