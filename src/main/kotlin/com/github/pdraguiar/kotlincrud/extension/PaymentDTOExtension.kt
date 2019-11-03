package com.github.pdraguiar.kotlincrud.extension

import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.dto.PaymentDTO

fun PaymentDTO.asEntity(): Payment = Payment(description, amount, expiresAt, recipient.asEntity(), id, paidAt)