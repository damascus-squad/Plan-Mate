package org.damascus.presentation.io

import logic.model.Project


interface Display {
    fun displayAllProjects(
        projects: List<Project>,
        label: String,
        contentSelector: ((Project) -> Map<String, Any?>)? = null
    )

    fun displayProjectsAsTable(projects: List<Project>)

}