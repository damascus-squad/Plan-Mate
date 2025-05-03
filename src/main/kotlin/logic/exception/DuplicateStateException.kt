package org.damascus.logic.exception

import java.util.*

class DuplicateStateException(id: UUID) : Exception("A state with ID ${id} already exists.")