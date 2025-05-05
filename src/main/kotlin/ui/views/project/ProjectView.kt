package org.damascus.ui.views.project

import logic.model.Project

interface ProjectView {
    fun createProject()
    fun showAllProjects() : Project?
}