import app.cash.sqldelight.gradle.SqlDelightExtension
import com.task.tracker.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.dependencies

class SqlDelightConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("app.cash.sqldelight")

        extensions.configure<SqlDelightExtension>("sqldelight") {
            databases {
                create("Database") {
                    packageName.set("com.task.tracker.core.database")

                    schemaOutputDirectory.set(
                        file("src/main/sqldelight/databases")
                    )
                }
            }
        }

        dependencies {
            "implementation"(libs.findLibrary("sqldelight-android-driver").get())
            "implementation"(libs.findLibrary("sqldelight-async-extensions").get())
            "implementation"(libs.findLibrary("sqldelight-coroutines-extensions").get())
        }

        tasks.matching {
            it.name.startsWith("ksp")
        }.configureEach {
            dependsOn(
                tasks.matching {
                    it.name.startsWith("generate") &&
                            it.name.contains("DatabaseInterface")
                }
            )
        }
    }
}
