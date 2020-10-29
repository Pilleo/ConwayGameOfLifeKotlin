import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    val kotlinVersion: String by System.getProperties()
    id("kotlinx-serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    id("io.spring.dependency-management") version System.getProperty("dependencyManagementPluginVersion")
    id("org.springframework.boot") version System.getProperty("springBootVersion")
    kotlin("plugin.spring") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("kvision") version kvisionVersion
}

extra["kotlin.version"] = "1.4.10"

version = "1.0.0-SNAPSHOT"
group = "com.example"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    maven { url = uri("https://dl.bintray.com/rjaros/kotlin") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()
val coroutinesVersion: String by project

val webDir = file("src/frontendMain/web")
val mainClassName = "com.example.MainKt"

kotlin {
    jvm("backend") {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }

        tasks.named<Test>("backendTest") {
            useJUnitPlatform()
        }

    }
    js("frontend") {
        browser {
            runTask {
                outputFileName = "main.bundle.js"
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 3000,
                    proxy = mapOf(
                        "/kv/*" to "http://localhost:8080",
                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
                    ),
                    contentBase = listOf("$buildDir/processedResources/frontend/main")
                )
            }
            webpackTask {
                outputFileName = "main.bundle.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("pl.treksoft:kvision-server-spring-boot:$kvisionVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
            kotlin.srcDir("build/generated-src/common")
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val backendMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("org.springframework.boot:spring-boot-starter")
                implementation("org.springframework.boot:spring-boot-devtools")
                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("pl.allegro.finance:tradukisto:1.4.0")
            }
        }
        val backendTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.springframework.boot:spring-boot-starter-test")

            }
            val frontendMain by getting {
                resources.srcDir(webDir)
                dependencies {
                    implementation("pl.treksoft:kvision:$kvisionVersion")
                    implementation("pl.treksoft:kvision-bootstrap:$kvisionVersion")
                    implementation("pl.treksoft:kvision-bootstrap-css:$kvisionVersion")
                    implementation("pl.treksoft:kvision-bootstrap-select:$kvisionVersion")
                }
                kotlin.srcDir("build/generated-src/frontend")
            }
            val frontendTest by getting {
                dependencies {
                    implementation(kotlin("test-js"))
                    implementation("pl.treksoft:kvision-testutils:$kvisionVersion:tests")
                }
            }
        }
    }

    fun getNodeJsBinaryExecutable(): String {
        val nodeDir = NodeJsRootPlugin.apply(project).nodeJsSetupTaskProvider.get().destination
        val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
        val nodeBinDir = if (isWindows) nodeDir else nodeDir.resolve("bin")
        val command = NodeJsRootPlugin.apply(project).nodeCommand
        val finalCommand = if (isWindows && command == "node") "node.exe" else command
        return nodeBinDir.resolve(finalCommand).absolutePath
    }

    tasks {
        create("generatePotFile", Exec::class) {
            dependsOn("compileKotlinFrontend")
            executable = getNodeJsBinaryExecutable()
            args("$buildDir/js/node_modules/gettext-extract/bin/gettext-extract")
            inputs.files(kotlin.sourceSets["frontendMain"].kotlin.files)
            outputs.file("$projectDir/src/frontendMain/resources/i18n/messages.pot")
        }
    }



    afterEvaluate {
        tasks {
            getByName("frontendProcessResources", Copy::class) {
                dependsOn("compileKotlinFrontend")
                exclude("**/*.pot")
                doLast("Convert PO to JSON") {
                    destinationDir.walkTopDown().filter {
                        it.isFile && it.extension == "po"
                    }.forEach {
                        exec {
                            executable = getNodeJsBinaryExecutable()
                            args(
                                "$buildDir/js/node_modules/gettext.js/bin/po2json",
                                it.absolutePath,
                                "${it.parent}/${it.nameWithoutExtension}.json"
                            )
                            println("Converted ${it.name} to ${it.nameWithoutExtension}.json")
                        }
                        it.delete()
                    }
                }
            }
            create("frontendArchive", Jar::class).apply {
                dependsOn("frontendBrowserProductionWebpack")
                group = "package"
                archiveAppendix.set("frontend")
                val distribution =
                    project.tasks.getByName(
                        "frontendBrowserProductionWebpack",
                        KotlinWebpack::class
                    ).destinationDirectory!!
                from(distribution) {
                    include("*.*")
                }
                from(webDir)
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                into("/public")
                inputs.files(distribution, webDir)
                outputs.file(archiveFile)
                manifest {
                    attributes(
                        mapOf(
                            "Implementation-Title" to rootProject.name,
                            "Implementation-Group" to rootProject.group,
                            "Implementation-Version" to rootProject.version,
                            "Timestamp" to System.currentTimeMillis()
                        )
                    )
                }
            }
            getByName("backendProcessResources", Copy::class) {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            getByName("bootJar", BootJar::class) {
                dependsOn("frontendArchive", "backendMainClasses")
                classpath = files(
                    kotlin.targets["backend"].compilations["main"].output.allOutputs +
                            project.configurations["backendRuntimeClasspath"] +
                            (project.tasks["frontendArchive"] as Jar).archiveFile
                )
            }
            getByName("jar", Jar::class).apply {
                dependsOn("bootJar")
            }
            getByName("bootRun", BootRun::class) {
                dependsOn("backendMainClasses")
                classpath = files(
                    kotlin.targets["backend"].compilations["main"].output.allOutputs +
                            project.configurations["backendRuntimeClasspath"]
                )
            }
            create("backendRun") {
                dependsOn("bootRun")
                group = "run"
            }
            getByName("compileKotlinBackend") {
                dependsOn("compileKotlinMetadata")
            }
            getByName("compileKotlinFrontend") {
                dependsOn("compileKotlinMetadata")
            }
        }
    }
}