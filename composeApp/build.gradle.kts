import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.application)
}

val appVersion = project.findProperty("app.version") as String

kotlin {
    androidTarget()
    jvm("desktop")

    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "dev.dettmer.deltip"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.dettmer.deltip"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = appVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "dev.dettmer.deltip.MainKt"
        jvmArgs += listOf(
            "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens", "java.desktop/sun.awt.windows=ALL-UNNAMED",
            "--enable-native-access=ALL-UNNAMED",
        )
        nativeDistributions {
            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Deb,
                TargetFormat.Rpm
            )
            packageName = "Deltip"
            packageVersion = appVersion
            description = "Mini-Tool zum Berechnen von Rabatten"
            vendor = "Dettmer"
            licenseFile.set(project.rootProject.file("LICENSE"))

            windows {
                menuGroup = "Deltip"
                upgradeUuid = "5b6a8e1c-7d3f-4f0a-9d3e-1d8d9f9c2e11"
                shortcut = true
                dirChooser = true
            }
            linux {
                shortcut = true
                menuGroup = "Utility"
                appCategory = "Utility"
            }
        }
    }
}
