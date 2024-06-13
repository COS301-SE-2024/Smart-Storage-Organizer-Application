
plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.example.smartstorageorganizer"
    compileSdk = 34
    android.buildFeatures.buildConfig =true

    defaultConfig {
        applicationId = "com.example.smartstorageorganizer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildConfigField( "String", "DefaultImage", "\"https://smart-storage-f0629f0176059-staging.s3.eu-north-1.amazonaws.com/public/ProfilePictures/622013117918730.jpeg?x-id=GetObject&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIA6ODVAP7O5ZR4SOSM%2F20240608%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Date=20240608T141540Z&X-Amz-Expires=604800&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEH4aCmV1LW5vcnRoLTEiRjBEAiB%2FfiiQsnR7L84F%2F%2F9%2B6ZabvImLyAOuuwUmotYk0SvJAwIgSUlahHdDQ4mJ2nCuKxvZkaDhUodUJOyMqAICU31vEAcqygQIFxAAGgw5OTIzODI4NDQ4OTMiDJQePNcXlk0BW0YBZCqnBCPSNy6i4C020OIDjcW119iK9an6x7lQH%2Fj8636Y%2F1GNwOI7d%2BVoRkTn87qQn871YrvKryMfJDyv52u4mwy3swtleQy4eo0qOSksuyTxFxPMehLVhalcKPF1uvBgfgfQltraI4MRihgC1LwRl05xKZ4cd0Uo%2FAKQqOsyy2IIsn5xMdaQfhqFs6fLfLXvcW369aLu2%2FXyUvwbPRqUl%2BK1tF5KzDkaLptfhM7fk8xYWM2peMhMRTJ27f%2FH4gPMTMijhWCEVgC0UbaGvLvrK8kzYzJZrnFFheCsH%2FxprdGKl1RShBrfXNolnkqqq8U%2FguDHXt3DI61S0ZvlyMn3xcf3jrh%2BMl4nOShnAEno%2B%2BPCD3Kx4X8tW8xUvoYd9MBvz7vq5UjplPZLGeFTawSbj%2BEu2iS%2B1BLPdJYjEoHE6eN9EjEQx2%2BceHkOGkjdKnilSTq0SMnkfYG2%2BkuzEw7v6TfKi%2FbxMaL%2F0wQQ10UtjhaT%2FMbZ07SWwgAQNEOEpR%2Fyv9tYUjLODbGtRW5RfGMrvtYue4n7DIaET8MM3T%2Bpu%2FqspsRxSxIDlflgaKR%2FpNG3mmd888jsBnGGc0aUh61YoacnlDSejRlSZNOM3e5MeN5VkJodJnLM1Ox0zIRFrzbxIkaxItpHwWJBQS11o9oGeVFwtmwi3MyKqKZmnFvx4wsTwawBkX9sZBc3ErtAwO8FhCNalU%2FqF4%2FQghywsexDXoeuj0vcG0fPDeMMMN3NkbMGOoYCEtVKL7HzobCvtkHWwrYtFZ87C6NtVkl3aKOw%2FqkTN3YCwGcQJmob3D68FSeiHgVORAAWPf9eXOShK9gmJqz%2FIjZseL%2FyRGUK%2B1qst9ks5%2FLMooLrHlZgRZRLnE9SBaaM3xXxwO5%2FxCzf1Isrl80IJULgdohWn1uNxYD440PHcC%2Fqt%2BadzRPxWd%2FSTeiJATSRYPWwypzTqFZ7q7tg6efbwGM0aLrn7lkiGlyQebx4o4qQNj1knhYqh3UoBj2Yv6ePXtLKW4Fkoea5dGsXe66946RJE0xAWtQFiF3fzSWynfryKZ4jLkhRMQikrq7PSVVBoY7yw9VU2ERcCyKcK0%2FjrE5rVvAlmA%3D%3D&X-Amz-SignedHeaders=host&X-Amz-Signature=9de3e4835fcae4ed1b02968bfabae2e2905387787c5dae9cb1faa37365b7d702\"");
        //buildConfigField( "String", "DefaultImage", defaultpicture)


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

    // for Cognito
    implementation ("com.amazonaws:aws-android-sdk-core:2.42.+")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.42.+")

    implementation ("com.amplifyframework:core:1.28.4")
    implementation ("com.amplifyframework:aws-auth-cognito:1.28.4")


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
    implementation ("com.github.bumptech.glide:glide:4.16.0")

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

    // AndroidX Test - Espresso Intents
    testImplementation("androidx.test.espresso:espresso-intents:3.4.0")

    // AndroidX Test - Espresso Idling Resource
    testImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0")

    // AndroidX Test - Espresso Contrib
    testImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    // AndroidX Test - Espresso Remote
    testImplementation("androidx.test.espresso:espresso-remote:3.4.0")

    implementation("com.hbb20:ccp:2.5.0");

    // Amplify API plugin for REST
    implementation("com.amplifyframework:aws-api:1.35.4")

    implementation("com.squareup.okhttp3:okhttp:4.9.2")

    implementation("com.google.code.gson:gson:2.8.8") // Use the latest version

    implementation("com.amplifyframework:aws-storage-s3:2.16.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")





}