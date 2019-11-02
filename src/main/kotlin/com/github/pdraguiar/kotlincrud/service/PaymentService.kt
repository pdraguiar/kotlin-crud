package com.github.pdraguiar.kotlincrud.service

import com.github.pdraguiar.kotlincrud.common.ErrorMessages
import com.github.pdraguiar.kotlincrud.extension.isValid
import com.github.pdraguiar.kotlincrud.extension.updateWith
import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.repository.PaymentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class PaymentService(private val paymentRepository: PaymentRepository) {
    fun findAll(): MutableList<Payment> = paymentRepository.findAll()

    fun countAll(): Long = paymentRepository.count()

    fun create(payment: Payment): Payment {
        if (!payment.isValid()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_OBJECT)

        payment.id = UUID.randomUUID().toString()

        return paymentRepository.save(payment)
    }

    fun update(payment: Payment): Payment {
        if (!payment.isValid()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_OBJECT)

        val paymentId = payment.id
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(ErrorMessages.FILL_UP_MANDATORY_FIELDS, "id"))
        val oldPayment = paymentExists(paymentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.RESOURCE_NOT_FOUND)

        return paymentRepository.save(oldPayment.updateWith(payment))
    }

    fun delete(id: String): Payment {
        val payment = paymentExists(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.RESOURCE_NOT_FOUND)

        paymentRepository.delete(payment)

        return payment
    }

    private fun paymentExists(id: String): Payment? = paymentRepository.findById(id).orElse(null)
}