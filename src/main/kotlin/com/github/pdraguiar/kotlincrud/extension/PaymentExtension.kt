package com.github.pdraguiar.kotlincrud.extension

import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.dto.PaymentDTO

fun Payment.asDTO(): PaymentDTO = PaymentDTO(description, amount, expiresAt, id, paidAt)

fun Payment.updateWith(otherPayment: Payment) : Payment{
    this.description = otherPayment.description
    this.amount = otherPayment.amount
    this.expiresAt = otherPayment.expiresAt
    this.paidAt = otherPayment.paidAt

    return this
}
