package dev.jimmymorales.featureflags.gradle.kmmbridge

import co.touchlab.faktory.versionmanager.VersionManager
import org.gradle.api.Project

internal object NoOpVersionManager : VersionManager {
    override fun getVersion(project: Project, versionPrefix: String): String = versionPrefix
    override fun recordVersion(project: Project, versionString: String) {}
}
