package org.damascus.data.mongodb

import com.mongodb.MongoException
import jdk.internal.joptsimple.internal.Messages.message

class CredentialsNotFound(message: String) : Exception(message)

class CollectionIsEmptyException() : MongoException("Mongo Collection is Empty")