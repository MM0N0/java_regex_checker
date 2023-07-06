// used Gradle 7.6
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

plugins {
    id("java")
}

val versionPropPath = "version.properties"
val versionProp = initVersionProp()
version = versionProp.getProperty("version")
val currDirectory = System.getProperty("user.dir").replace("\\","/")
val artifactName = "java_regex_checker-$version.jar"

group = "app"
var mainClass = "app.App"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    // https://mvnrepository.com/artifact/info.picocli/picocli
    implementation("info.picocli:picocli:4.7.1")


}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// build executable jar
tasks.jar {
    manifest.attributes(mapOf(Pair("Main-Class", mainClass)))
}
// build fat jar
tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

// load Version
fun initVersionProp():Properties {
    return Properties().apply {
        load(FileInputStream(versionPropPath))
    }
}

// set Version
tasks.register("setVersion") {
    val version = System.getProperty("version")
    try {
        versionProp.setProperty("version", version)
    } catch (e:NullPointerException) {
        println("setVersion: throws Nullpointer on Windows for some reason. Only use in Linux/wsl")
    }
    versionProp.store(FileOutputStream(versionPropPath), null)
}

// get Version
tasks.register("getVersion"){
    println(version)
}

// get Version, but cut off Snapshot (Ex.: 1.1.5-SNAPSHOT - > 1.1.5)
tasks.register("getVersionWithoutSnapshot"){
    project.exec {
        commandLine("bash", "-c", "echo $version | sed -r 's/([^-]*)-.*/\\1/g'")
    }
}

// get Version, but cut off Patch-Version (Ex.: 0.2.1 -> 0.2)
tasks.register("getReleaseBranchName"){
    project.exec {
        commandLine("bash", "-c", "echo $version | sed -r 's/([^\\.]*\\.[^\\.]*)\\..*/\\1/g'")
    }
}