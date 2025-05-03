package org.damascus.logic.repository

import logic.model.Project
import java.util.UUID

interface ProjectRepository {
    fun create(project: Project): Boolean
    fun update(projectId: UUID, project: Project): Boolean
    fun delete(projectId: UUID): Boolean
    fun exists(projectId: UUID): Boolean
    fun get(projectId: UUID): Project
    fun getAll(): List<Project>
    fun getAllProjectsByMateId(mateId: UUID): List<Project>
}