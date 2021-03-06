package com.n26.controller.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import java.math.BigDecimal


@JsonComponent
class BigDecimalSerializer : JsonSerializer<BigDecimal>() {
    override fun serialize(value: BigDecimal, jgen: JsonGenerator, provider: SerializerProvider) =
            jgen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString())
}