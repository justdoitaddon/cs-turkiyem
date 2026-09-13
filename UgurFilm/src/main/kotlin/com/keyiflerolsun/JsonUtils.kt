package com.keyiflerolsun

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val myMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

inline fun <reified T : Any> tryParseJson(text: String): T? {
    return try {
        myMapper.readValue(text, object : TypeReference<T>() {})
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T : Any> parseJson(text: String): T {
    return myMapper.readValue(text, object : TypeReference<T>() {})
}
