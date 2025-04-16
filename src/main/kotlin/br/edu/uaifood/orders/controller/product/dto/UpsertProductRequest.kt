package br.edu.uaifood.orders.controller.product.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UpsertProductRequest(
    val id: UUID,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    @JsonProperty("image_url")
    val imageUrl: String
)