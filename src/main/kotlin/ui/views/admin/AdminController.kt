package org.damascus.ui.views.admin

import logic.model.User

interface AdminController {
    fun createProject(currentUser: User)
}