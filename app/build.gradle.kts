plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.miempresa.mifpsgame"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.miempresa.mifpsgame"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    androidResources {
        noCompress += listOf("j3o", "bvhist", "mesh.xml", "skeleton.xml")
    }
}

dependencies {
    val jmeVersion = "3.6.1-stable"

    // jMonkeyEngine - Núcleo
    implementation("org.jmonkeyengine:jme3-core:$jmeVersion")
    implementation("org.jmonkeyengine:jme3-android:$jmeVersion")
    implementation("org.jmonkeyengine:jme3-android-native:$jmeVersion")
    implementation("com.simsilica:lemur:1.16.0") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    // Física con Bullet
    implementation("org.jmonkeyengine:jme3-jbullet:$jmeVersion")

    // Material components
    implementation("com.google.android.material:material:1.9.0")

    // Efectos visuales
    implementation("org.jmonkeyengine:jme3-effects:$jmeVersion")
}

configurations.all {
    resolutionStrategy {
        force("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    }
}