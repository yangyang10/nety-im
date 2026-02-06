pluginManagement {
    repositories {
        gradlePluginPortal() // 必加！Protobuf/Kotlin插件从这下载
        google()
        mavenCentral()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "netty-im"
include(":app")
include(":libs:lib_websocket")
