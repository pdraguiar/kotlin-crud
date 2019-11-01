package com.github.pdraguiar.kotlincrud.model.dto

data class PaymentListDTO(val payments: List<PaymentDTO>,
                          override val total: Long) : ListDTO<PaymentDTO>(total)