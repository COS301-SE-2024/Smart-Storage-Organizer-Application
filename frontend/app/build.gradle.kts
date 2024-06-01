
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smartstorageorganizer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartstorageorganizer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    compileOptions {
        // Support for Java 8 features
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("com.amplifyframework.ui:authenticator:1.2.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("com.amplifyframework:aws-auth-cognito:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation ("com.airbnb.android:lottie:6.4.1")

    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito:mockito-inline:4.2.0")

    // AndroidX Test - Core, Runner, and Rules
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("androidx.test:runner:1.4.0")
    testImplementation("androidx.test:rules:1.4.0")

    // AndroidX Test - ExtJUnit for JUnit4 integration
    testImplementation("androidx.test.ext:junit:1.1.3")

    // AndroidX Test - Espresso
    testImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.amplifyframework:aws-api:2.16.1")
    implementation("com.amplifyframework:aws-auth-cognito:2.16.1")
    testImplementation("androidx.test.espresso:espresso-core:3.4.0");

    // AndroidX Test - Espresso Intents
    testImplementation("androidx.test.espresso:espresso-intents:3.4.0")

    // AndroidX Test - Espresso Idling Resource
    testImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0")

    // AndroidX Test - Espresso Contrib
    testImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    // AndroidX Test - Espresso Remote
    testImplementation("androidx.test.espresso:espresso-remote:3.4.0")

    implementation("com.hbb20:ccp:2.5.0");

}