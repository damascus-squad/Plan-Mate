package org.damascus.data.repo

import java.util.*

class ProjectNotFoundException(projectId: UUID) :
    RuntimeException("Project with ID $projectId not found")