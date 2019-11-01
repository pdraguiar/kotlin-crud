package com.github.pdraguiar.kotlincrud

import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.dto.PaymentListDTO
import com.github.pdraguiar.kotlincrud.repository.PaymentRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @MockkBean private lateinit var paymentRepository: PaymentRepository

    lateinit var firstPayment: Payment
    lateinit var secondPayment: Payment
    lateinit var payments: List<Payment>
    var paymentsSize: Long = 0L

    @BeforeAll
    fun init() {
        firstPayment = Payment(description = "some description",
                amount = BigDecimal(100.00),
                expiresAt = LocalDate.parse("2019-10-30"),
                id = UUID.randomUUID().toString(),
                paidAt = LocalDate.parse("2019-10-30").atStartOfDay())

        secondPayment = Payment(description = "some other description",
                amount = BigDecimal(200.00),
                expiresAt = LocalDate.parse("2019-10-31"),
                id = UUID.randomUUID().toString(),
                paidAt = LocalDate.parse("2019-10-31").atStartOfDay())

        payments = listOf(firstPayment, secondPayment)
        paymentsSize = payments.size.toLong()
    }

    @Test
	fun `When list payments then return all payments`() {
        every {paymentRepository.findAll()} returns payments
        every {paymentRepository.count()} returns paymentsSize

        val response = restTemplate.getForEntity<PaymentListDTO>("/api/payments")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.total).isEqualTo(paymentsSize)
	}
}
