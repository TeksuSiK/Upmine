plugins {
    id("java")
}

group = "pl.teksusik.upmine"
version = "1.0-SNAPSHOT"

val slf4jVersion = "2.0.17"
val javalinVersion = "6.7.0"
val dockerJavaVersion = "3.7.0"

repositories {
    mavenCentral()
    maven(url = "https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.7")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.13")

    implementation("org.quartz-scheduler:quartz:2.5.2")

    implementation("io.javalin:javalin:$javalinVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("io.javalin.community.openapi:javalin-openapi-plugin:$javalinVersion")
    implementation("io.javalin.community.openapi:javalin-swagger-plugin:$javalinVersion")
    annotationProcessor("io.javalin.community.openapi:openapi-annotation-processor:$javalinVersion")

    implementation("com.github.docker-java:docker-java:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:$dockerJavaVersion")

    testImplementation(platform("org.junit:junit-bom"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}