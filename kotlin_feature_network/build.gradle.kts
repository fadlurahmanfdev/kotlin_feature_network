plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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

    api("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // chucker
    api("com.github.chuckerteam.chucker:library:4.0.0")
}

publishing {
    publications {
        register<MavenPublication>("release"){
            groupId = "co.id.fadlurahmanfdev"
            artifactId = "kotlin_feature_network"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}