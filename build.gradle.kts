plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "kr.hyfata.najoan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.json:json:20231013")
    implementation("commons-io:commons-io:2.14.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "kr.hyfata.najoan.async.filecopy.AsyncFileCopy"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}