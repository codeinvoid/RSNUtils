plugins {
    id("fabric-loom")
    kotlin("jvm").version(System.getProperty("kotlin_version"))
}
base { archivesName.set(project.extra["archives_base_name"] as String) }
version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}
dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, null, "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", project.extra["fabric_version"] as String)
    modImplementation("net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String)
    modImplementation("net.silkmc:silk-commands:1.9.5")
    modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.google.code.gson:gson:2.10.1")

    modApi("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    include("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
}
tasks {
    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = javaVersion.toString() } }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName.get()}" } } }
    processResources {
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.extra["mod_version"] as String, "fabricloader" to project.extra["loader_version"] as String, "fabric_api" to project.extra["fabric_version"] as String, "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String, "minecraft" to project.extra["minecraft_version"] as String, "java" to project.extra["java_version"] as String)) }
        filesMatching("*.mixins.json") { expand(mutableMapOf("java" to project.extra["java_version"] as String)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
