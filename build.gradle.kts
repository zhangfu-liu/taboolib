// 导入必要的 Gradle 任务类和 TabooLib 相关类
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.Basic
import io.izzel.taboolib.gradle.Bukkit
import io.izzel.taboolib.gradle.BukkitUI
import io.izzel.taboolib.gradle.BukkitUtil
import io.izzel.taboolib.gradle.XSeries
import io.izzel.taboolib.gradle.MinecraftChat
import io.izzel.taboolib.gradle.CommandHelper

// 声明项目使用的插件
plugins {
    java  // Java 项目支持
    id("io.izzel.taboolib") version "2.0.18"  // TabooLib 框架支持
    id("org.jetbrains.kotlin.jvm") version "1.8.22"  // Kotlin 支持
}

// TabooLib 框架配置
taboolib {
    // 环境配置，安装必要的模块
    env {
        install(Basic)  // 基础功能模块
        install(Bukkit)  // Bukkit API 支持
        install(BukkitUI)  // UI 相关功能
        install(BukkitUtil)  // 实用工具
        install(XSeries)  // 跨版本物品支持
        install(MinecraftChat)  // 聊天相关功能
        install(CommandHelper)  // 命令助手
    }
    // 插件描述配置
    description {
        name = "untitled2"  // 插件名称
    }
    // TabooLib 版本配置
    version { taboolib = "6.2.0-beta22" }
}

// 配置 Maven 仓库
repositories {
    mavenCentral()  // 使用 Maven 中央仓库
    mavenLocal()    // 本地 Maven 仓库
}

// 项目依赖配置
dependencies {
    // 引入 gson
    compileOnly("com.google.code.gson:gson:2.10.1")
    // TabooLib 核心依赖
    compileOnly("ink.ptms.core:v12001:12001:mapped")  // 映射版本
    compileOnly("ink.ptms.core:v12001:12001:universal")  // 通用版本
    compileOnly(kotlin("stdlib"))  // Kotlin 标准库
    compileOnly(fileTree("libs"))  // 本地 lib 目录下的所有依赖
}

// Java 编译任务配置
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"  // 设置源文件编码为 UTF-8
}

// Kotlin 编译任务配置
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"  // 设置目标 JVM 版本
        freeCompilerArgs = listOf("-Xjvm-default=all")  // 启用所有接口默认方法
    }
}

// Java 插件约定配置
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8  // 源代码兼容性版本
    targetCompatibility = JavaVersion.VERSION_1_8  // 目标兼容性版本
}
