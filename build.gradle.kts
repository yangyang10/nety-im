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
            // 发布到 Central Publishing Portal 的 staging endpoint
            nexusUrl.set(uri("https://central.sonatype.com/api/v1/maven/"))

            // 使用 Central Publishing Portal token
            username = project.findProperty("centralUsername")?.toString() ?: ""
            password = project.findProperty("centralPassword")?.toString() ?: ""
        }
    }

    // 所有子项目的 groupId
    packageGroup.set("io.github.yangyang10")
}

