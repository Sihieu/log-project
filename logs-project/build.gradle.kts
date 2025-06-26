plugins {
    java
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    
    implementation("org.postgresql:postgresql:42.7.3")

    implementation("org.opensearch.client:opensearch-java:2.12.0")

    implementation("org.opensearch.client:opensearch-rest-client:2.12.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    implementation("org.apache.httpcomponents.core5:httpcore5:5.2.4")

    implementation("org.slf4j:slf4j-simple:2.0.7")
}

tasks.register<JavaExec>("runIngestor") {
    group = "Application"
    description = "Runs the LogIngestor to send data to OpenSearch."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("LogIngestor")
}

tasks.register<JavaExec>("runSearcher") {
    group = "Application"
    description = "Runs the LogSearcher to query data from OpenSearch."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("LogSearcher")

}

tasks.register<JavaExec>("runDashboard") {
    group = "Application"
    description = "Runs the LogDashboard to query and display logs as JSON."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.search_log.LogDashboard")
}

