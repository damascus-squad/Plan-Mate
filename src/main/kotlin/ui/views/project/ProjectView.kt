package org.damascus.ui.views.project

import logic.model.Project

interface ProjectView {

    fun displayAllProjects(
        projects: List<Project>,
        label: String,
        contentSelector: ((Project) -> Map<String, Any?>)? = null
    )

    fun displayProjectsAsTable(projects: List<Project>)

}