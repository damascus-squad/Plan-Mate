package org.damascus.logic.exception

import java.util.*

class StateNotFoundException(id: UUID) :Exception("State with id ${id} not found.")