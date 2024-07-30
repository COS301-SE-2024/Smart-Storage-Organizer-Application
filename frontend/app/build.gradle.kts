import com.android.build.api.dsl.Packaging
import com.android.build.gradle.internal.packaging.defaultExcludes
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.reader(Charsets.UTF_8).use { reader ->
        localProperties.load(reader)
    }
}

android {

    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/ASL-2.0.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/mimetypes.default"
            excludes+="META-INF/mailcap.default"
            excludes+="META-INF/LICENSE.md"
            excludes+="META-INF/DEPENDENCIES"
            excludes+="META-INF/jing-copying.html"
            excludes+="META-INF/LGPL-3.0.txt"
            // Add other exclusions if necessary
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

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


        buildConfigField( "String", "DefaultImage", "\"${localProperties["defaultpicture"]}\"");
        //buildConfigField( "String", "DefaultImage", defaultpicture)
        buildConfigField( "String", "DeleteCategoryEndPoint", "\"${localProperties["DeleteCategoryEndPoint"]}\"");

        buildConfigField( "String", "RecommendCategoryEndPoint", "\"${localProperties["RecommendCategoryEndPoint"]}\"");

        buildConfigField( "String", "FetchCategoryEndPoint", "\"${localProperties["FetchCategoryEndPoint"]}\"");

        buildConfigField( "String", "AddCategoryEndPoint", "\"${localProperties["AddCategoryEndPoint"]}\"");

        buildConfigField( "String", "EditItemEndPoint", "\"${localProperties["EditItemEndPoint"]}\"");

        buildConfigField( "String", "FetchByEmailEndPoint", "\"${localProperties["FetchByEmailEndPoint"]}\"");


        buildConfigField( "String", "AddItemEndPoint", "\"${localProperties["AddItemEndPoint"]}\"");
        buildConfigField("String", "AddUnitEndPoint", "\"${localProperties["AddUnitEndPoint"]}\"");


//        buildConfigField( "String", "ChangeQuantityEndPoint", "\"${localProperties["ChangeQuantityEndPoint"]}\"");
        buildConfigField( "String", "DeleteItemEndPoint", "\"${localProperties["DeleteItemEndPoint"]}\"");
        buildConfigField("String", "CategoryFilterEndPoint", "\"${localProperties["CategoryFilterEndPoint"]}\"");

        buildConfigField("String", "SubCategoryFilterEndPoint", "\"${localProperties["SubCategoryFilterEndPoint"]}\"");
        buildConfigField("String", "CategoryToUncategorized", "\"${localProperties["CategoryToUncategorized"]}\"");
        buildConfigField("String", "SubcategoryToUncategorized", "\"${localProperties["SubcategoryToUncategorized"]}\"");
        buildConfigField("String", "ModifyCategoryName", "\"${localProperties["ModifyCategoryName"]}\"");
        buildConfigField("String", "FetchAllEndPoint", "\"${localProperties["FetchAllEndPoint"]}\"");

        buildConfigField("String", "AddColourEndPoint", "\"${localProperties["AddColourEndPoint"]}\"");

        buildConfigField("String", "AddItemToColourEndPoint", "\"${localProperties["AddItemToColourEndPoint"]}\"");

        buildConfigField("String", "FetchByColourEndPoint", "\"${localProperties["FetchByColourEndPoint"]}\"");

        buildConfigField("String", "DeleteColour", "\"${localProperties["DeleteColour"]}\"");
        buildConfigField("String", "GenerateQrcode", "\"${localProperties["GenerateQrcode"]}\"");

        buildConfigField("String", "FetchByIDEndPoint", "\"${localProperties["FetchByIDEndPoint"]}\"");

        buildConfigField("String", "GenerateBarcode", "\"${localProperties["GenerateBarcode"]}\"");

        buildConfigField("String", "SetRoleEndPoint", "\"${localProperties["SetRoleEndPoint"]}\"");
        buildConfigField("String", "getUserRoles", "\"${localProperties["getUserRoles"]}\"");
        buildConfigField("String", "getUserVerifIcationStatus", "\"${localProperties["getUserVerifIcationStatus"]}\"");
        buildConfigField("String", "setUserToUnverified", "\"${localProperties["setUserToUnverified"]}\"");
        buildConfigField("String", "setUserToVerified", "\"${localProperties["setUserToVerified"]}\"");
        buildConfigField("String", "getUsersInGroup", "\"${localProperties["getUsersInGroup"]}\"");



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
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.12.2")

    implementation ("com.github.yukuku:ambilwarna:2.0.1");
    implementation ("com.facebook.shimmer:shimmer:0.5.0")


    implementation("org.slf4j:slf4j-api:1.7.30")

//    for facebook Shimmeer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")


    // https://mvnrepository.com/artifact/org.keycloak/keycloak-admin-client
    // https://mvnrepository.com/artifact/org.keycloak/keycloak-admin-client

    //implementation("org.keycloak:keycloak-core:25.0.1")
    implementation("org.keycloak:keycloak-admin-client:25.0.1") {
        exclude(group = "com.sun.activation", module = "jakarta.activation")
        exclude(group = "org.eclipse.angus", module = "angus-activation")
    }

    implementation("org.slf4j:slf4j-log4j12:1.7.30")
    implementation("log4j:log4j:1.2.17")
//    implementation ("com.github.yuriy-budiyev:code-scanner:2.3.0")
    // Example for using ZXing
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.3.3")
    implementation("jakarta.activation:jakarta.activation-api:2.1.2") {
        exclude(group = "com.sun.activation", module = "jakarta.activation")
        exclude(group = "org.eclipse.angus", module = "angus-activation")
    }

    // Angus Activation
    implementation("org.eclipse.angus:angus-activation:1.0.0") {
        exclude(group = "com.sun.activation", module = "jakarta.activation")
        exclude(group = "jakarta.activation", module = "jakarta.activation-api")
    }

    testImplementation ("org.robolectric:robolectric:4.6.1")
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


    implementation("com.squareup.okhttp3:okhttp:4.9.2")

    implementation("com.google.code.gson:gson:2.8.8") // Use the latest version

    implementation("com.amplifyframework:aws-storage-s3:2.16.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")





}