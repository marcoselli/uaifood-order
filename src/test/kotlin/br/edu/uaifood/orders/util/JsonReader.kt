package br.edu.uaifood.orders.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class JsonReader(
    val resourceLoader: ResourceLoader,
    val objectMapper: ObjectMapper
) {
    fun <T> importClass(fileName: String, clazz: Class<T>): T {
        val resource = resourceLoader.getResource("classpath:/json/$fileName")
        return objectMapper.readValue(resource.inputStream, clazz)
    }

    fun import(fileName: String): String {
        val resource = resourceLoader.getResource("classpath:/json/$fileName")
        return resource.inputStream.bufferedReader().use { it.readText() }
    }
}