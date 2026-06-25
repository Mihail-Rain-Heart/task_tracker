package com.task.tracker.core.network.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

internal object InstantMillisSerializer : KSerializer<Instant> {

    override val descriptor = PrimitiveSerialDescriptor(
        "Instant",
        PrimitiveKind.LONG
    )

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }

    override fun serialize(
        encoder: Encoder,
        value: Instant
    ) {
        encoder.encodeLong(value.toEpochMilliseconds())
    }
}
