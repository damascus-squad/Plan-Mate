package org.damascus.ui.io

import logic.model.Project
import org.damascus.ui.util.UiAction

interface Display {
    fun displayMenu(uiActionList: List<UiAction>, menuTitle: String)

    fun displayAllProjects(
        projects: List<Project>,
        label: String,
        contentSelector: ((Project) -> Map<String, Any?>)? = null
    )

    fun displayProjectsAsTable(projects: List<Project>)

}