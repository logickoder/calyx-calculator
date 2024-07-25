import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.ksp) apply false
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.layout.buildDirectory)
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
            jvmTarget.set(JvmTarget.fromTarget("17"))
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        }
    }
}