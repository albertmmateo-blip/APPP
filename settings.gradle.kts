pluginManagement {
    repositories {
        maven { url = uri("/usr/local/lib/android/sdk/extras/google/m2repository") }
        maven { url = uri("/usr/local/lib/android/sdk/extras/android/m2repository") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("/usr/local/lib/android/sdk/extras/google/m2repository") }
        maven { url = uri("/usr/local/lib/android/sdk/extras/android/m2repository") }
        google()
        mavenCentral()
    }
}

rootProject.name = "ApppAvisos"
include(":app")
