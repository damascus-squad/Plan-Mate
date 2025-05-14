package org.damascus.logic.repo

import org.damascus.logic.model.Project
import java.util.*

interface ProjectRepository {
    fun create(project: Project): Boolean
    fun update(projectId: UUID, project: Project): Boolean
    fun delete(projectId: UUID): Boolean
    fun exists(projectId: UUID): Boolean
    fun get(projectId: UUID): Project
    fun getAll(): List<Project>
    fun getAllProjectsByMateId(mateId: UUID): List<Project>
}