val velocityVersion = "3.2.0-SNAPSHOT"

dependencies {
  compileOnly(project(":sonar-api"))
  compileOnly(project(":sonar-common"))

  compileOnly("com.velocitypowered:velocity-proxy:$velocityVersion") // Proxy module

  compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
  annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

  testCompileOnly("com.velocitypowered:velocity-api:$velocityVersion")
  testAnnotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")

  // Implement bStats.org for metrics
  implementation("org.bstats:bstats-velocity:3.0.2")
}

// Velocity supports Java 17
kotlin {
  jvmToolchain(17)
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
