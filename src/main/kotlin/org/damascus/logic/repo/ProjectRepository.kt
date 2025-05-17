package org.damascus.logic.repo

import org.damascus.logic.model.Project
import java.util.*

interface ProjectRepository {
    suspend fun create(project: Project): Boolean
    suspend fun update(projectId: UUID, project: Project): Boolean
    suspend fun delete(projectId: UUID): Boolean
    suspend fun exists(projectId: UUID): Boolean
    suspend fun get(projectId: UUID): Project
    suspend fun getAll(): List<Project>
    suspend fun getAllProjectsByMateId(mateId: UUID): List<Project>
}