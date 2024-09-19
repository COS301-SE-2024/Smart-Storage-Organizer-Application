import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
    }
}

plugins {
    alias(libs.plugins.android.application)
}
apply(plugin = "com.google.gms.google-services") // Apply Google Services plugin here

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.reader(Charsets.UTF_8).use { reader ->
        localProperties.load(reader)
    }
}

android {

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }




    namespace = "com.example.smartstorageorganizer"
    compileSdk = 34
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.smartstorageorganizer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","changeCategory","\"${localProperties["changeCategory"]}\"")
        buildConfigField("String","getParentCategory","\"${localProperties["getParentCategory"]}\"")
        buildConfigField("String","getCategoryName","\"${localProperties["getCategoryName"]}\"")
        buildConfigField("String","GetUnitConstraints","\"${localProperties["GetUnitConstraints"]}\"")
        buildConfigField("String","AddUnitEndpoint","\"${localProperties["AddUnitEndpoint"]}\"")
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

        buildConfigField("String", "RecommendMultipleEndPoint", "\"${localProperties["RecommendMultipleEndPoint"]}\"");
        buildConfigField("String", "CreateCategoryAIEndPoint", "\"${localProperties["CreateCategoryAIEndPoint"]}\"");

        buildConfigField("String", "FetchUncategorizedEndPoint", "\"${localProperties["FetchUncategorizedEndPoint"]}\"");
        buildConfigField("String", "getCategoryName", "\"${localProperties["getCategoryName"]}\"");
        buildConfigField("String", "SearchEndPoint", "\"${localProperties["SearchEndPoint"]}\"");
        buildConfigField("String", "GetAllUnits", "\"${localProperties["GetAllUnits"]}\"");
        buildConfigField("String", "GetCategoriesOfUnits", "\"${localProperties["GetCategoriesOfUnits"]}\"");
        buildConfigField("String", "GetItemsUnderUnit", "\"${localProperties["GetItemsUnderUnit"]}\"");
        buildConfigField("String", "CreateOrganization", "\"${localProperties["CreateOrganization"]}\"");
        buildConfigField("String", "FetchOrganization", "\"${localProperties["FetchOrganization"]}\"");
        buildConfigField("String", "FetchAllColours", "\"${localProperties["FetchAllColours"]}\"");






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
    implementation("com.google.firebase:firebase-messaging:latest-version")
    implementation(libs.firebase.database)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.12.2")
    implementation("com.github.yukuku:ambilwarna:2.0.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")
    implementation("log4j:log4j:1.2.17")

//    implementation ("com.github.yuriy-budiyev:code-scanner:2.3.0")
    // Example for using ZXing
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.3.3")

    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.firebase:firebase-core:21.1.1")



    // for Cognito
    implementation ("com.amazonaws:aws-android-sdk-core:2.42.+")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.42.+")

    implementation ("com.amplifyframework:core:1.28.4")
//    implementation ("com.amplifyframework:aws-auth-cognito:1.28.4")
    implementation ("com.amplifyframework:aws-auth-cognito:latest-version")



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
    implementation("com.amplifyframework:aws-auth-cognito:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")

    implementation ("com.airbnb.android:lottie:6.4.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    implementation ("com.caverock:androidsvg:1.4")

    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito:mockito-inline:4.2.0")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("androidx.test:runner:1.4.0")
    testImplementation("androidx.test:rules:1.4.0")
    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.amplifyframework:aws-api:2.16.1")
    testImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    testImplementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    testImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    testImplementation("androidx.test.espresso:espresso-remote:3.4.0")
    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.amplifyframework:aws-api:1.35.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.amplifyframework:aws-storage-s3:2.16.1")
    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.25.+")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

//    FirebaseMessaging Tests
    testImplementation ("org.robolectric:robolectric:4.6.1")
    testImplementation ("androidx.test:core:1.4.0")
    testImplementation ("androidx.test.ext:junit:1.1.3")
    testImplementation ("androidx.test:runner:1.4.0")
    testImplementation ("androidx.test:rules:1.4.0")

    // oneSignal
    implementation ("com.onesignal:OneSignal:[5.0.0, 5.99.99]")
    implementation("com.onesignal:OneSignal:4.0.0")
}