package org.damascus.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class KoverIgnore(@Suppress("unused") val reason: String)