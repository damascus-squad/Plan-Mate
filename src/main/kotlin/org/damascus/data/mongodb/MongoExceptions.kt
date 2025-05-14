package org.damascus.data.mongodb

import com.mongodb.MongoException

class CredentialsNotFound(message: String) : Exception(message)

class NoSuchDocumentException(message: String) : MongoException(message)