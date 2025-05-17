package org.damascus.data.mongodb

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import java.time.Instant
import java.time.ZoneOffset

class KotlinLocalDateTimeCodec : Codec<LocalDateTime> {
    override fun encode(writer: BsonWriter, value: LocalDateTime, encoderContext: EncoderContext) {
        val javaLdt = value.toJavaLocalDateTime()
        writer.writeDateTime(javaLdt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): LocalDateTime {
        val instant = Instant.ofEpochMilli(reader.readDateTime())
        val javaLdt = java.time.LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        return javaLdt.toKotlinLocalDateTime()
    }

    override fun getEncoderClass(): Class<LocalDateTime> = LocalDateTime::class.java
}