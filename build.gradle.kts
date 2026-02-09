plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username = project.findProperty("sonatypeUsername")?.toString() ?: ""
            password = project.findProperty("sonatypePassword")?.toString() ?: ""
        }
    }

    // 发布所有子项目的 publications
    // 使用这个可以简化配置
    packageGroup.set("com.xiaotimel.im")
}

