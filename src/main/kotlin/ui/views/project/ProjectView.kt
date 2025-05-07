package ui.views.project

import logic.model.Project
import logic.model.User

interface ProjectView {
    fun showAllProjects(currentUser: User): Project
}