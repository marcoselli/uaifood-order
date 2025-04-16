package br.edu.uaifood.orders.controller.order.dto

import br.edu.uaifood.orders.controller.product.dto.UpsertProductRequest

class OrderRequest(
    var products: List<UpsertProductRequest> = emptyList()
)