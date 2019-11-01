package com.github.pdraguiar.kotlincrud.controller

import com.github.pdraguiar.kotlincrud.extension.asDTO
import com.github.pdraguiar.kotlincrud.extension.asEntity
import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.dto.ListDTO
import com.github.pdraguiar.kotlincrud.model.dto.PaymentDTO
import com.github.pdraguiar.kotlincrud.model.dto.PaymentListDTO
import com.github.pdraguiar.kotlincrud.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping
    fun findAll(): ListDTO<PaymentDTO> {
        val payments: List<PaymentDTO> = paymentService.findAll().map { p -> p.asDTO() }
        val total: Long = paymentService.countAll()

        return PaymentListDTO(payments, total)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody paymentDTO: PaymentDTO): PaymentDTO
            = paymentService.create(paymentDTO.asEntity()).asDTO()

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody paymentDTO: PaymentDTO): PaymentDTO {
        val payment: Payment = paymentDTO.asEntity()
        payment.id = id

        return paymentService.update(payment).asDTO()
    }

    @DeleteMapping("/{id}")
    fun delete(id: String): PaymentDTO = paymentService.delete(id).asDTO()
}
