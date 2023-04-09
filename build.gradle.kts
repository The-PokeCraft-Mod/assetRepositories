plugins {
    id("java")
    id("application")
}

group = "com.thepokecraftmod"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("org.tukaani:xz:1.9")
}

application {
    mainClass.set("com.thepokecraftmod.tools.Main")
}