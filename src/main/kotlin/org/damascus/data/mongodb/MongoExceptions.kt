package org.damascus.data.mongodb

import org.damascus.annotation.KoverIgnore

@KoverIgnore("TBD")
class DatabaseCredentialsNotFound(message: String) : Exception(message)

class MongoDocumentNotFound : Exception()