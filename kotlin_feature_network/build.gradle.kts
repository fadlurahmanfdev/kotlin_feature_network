import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")

    id("com.vanniktech.maven.publish") version "0.29.0"
}

android {
    namespace = "co.id.fadlurahmanfdev.kotlin_feature_network"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // retorift
    val retrofitVersion = "2.11.0"
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    api("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    api("com.squareup.retrofit2:adapter-rxjava2:2.11.0")

    api("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // chucker
    api("com.github.chuckerteam.chucker:library:4.0.0")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("com.fadlurahmanfdev", "kotlin_feature_network", "0.0.1")

    pom {
        name.set("Kotlin Library Feature Network")
        description.set("A comprehensive library that simplifies network and HTTP requests in Kotlin. It includes an easy-to-use OkHttp client builder, Chucker interceptor for debugging, and robust SSL handling. This library streamlines the setup of network layers in your app, ensuring secure and efficient communication with minimal configuration.")
        inceptionYear.set("2024")
        url.set("https://github.com/fadlurahmanfdev/kotlin_feature_network/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("fadlurahmanfdev")
                name.set("Taufik Fadlurahman Fajari")
                url.set("https://github.com/fadlurahmanfdev/")
            }
        }
        scm {
            url.set("https://github.com/fadlurahmanfdev/kotlin_feature_network/")
            connection.set("scm:git:git://github.com/fadlurahmanfdev/kotlin_feature_network.git")
            developerConnection.set("scm:git:ssh://git@github.com/fadlurahmanfdev/kotlin_feature_network.git")
        }
    }
}