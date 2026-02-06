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
    packageGroup = "com.xiaotimel.im"
}