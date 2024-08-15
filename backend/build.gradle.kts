plugins {
    id("java")
}

group = "pl.teksusik.upmine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.2")

    implementation("org.quartz-scheduler:quartz:2.3.0")

    implementation("io.javalin:javalin:6.1.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("com.github.docker-java:docker-java:3.4.0")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.4.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}