package com.github.pdraguiar.kotlincrud

import com.github.pdraguiar.kotlincrud.extension.asDTO
import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.dto.PaymentDTO
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
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
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
    lateinit var paymentWithInvalidAMount: PaymentDTO

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

        paymentWithInvalidAMount = PaymentDTO(description = "a description",
                amount = BigDecimal(-1),
                expiresAt = LocalDate.parse("2019-10-05"),
                id = UUID.randomUUID().toString(),
                paidAt = LocalDate.parse("2019-10-31").atStartOfDay())
    }

    @Test
	fun `When list payments then return all payments`() {
        every {paymentRepository.findAll()} returns payments
        every {paymentRepository.count()} returns paymentsSize

        val response = restTemplate.getForEntity<PaymentListDTO>("/api/payments")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.total).isEqualTo(paymentsSize)
	}

    @Test
    fun `When create payment then http status is CREATED`() {
        every { paymentRepository.save(any<Payment>()) } returns firstPayment

        val response = restTemplate.postForEntity<PaymentDTO>("/api/payments", firstPayment.asDTO())

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isEqualTo(firstPayment.asDTO())
    }

    @Test
    fun `When create payment with zero or negative amount then http status is BadRequest`() {
        val response = restTemplate.postForEntity<Any>("/api/payments", paymentWithInvalidAMount)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `When update payment then http status is OK`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.of(firstPayment)
        every { paymentRepository.save(any<Payment>()) } returns firstPayment

        val request = HttpEntity<PaymentDTO>(firstPayment.asDTO(), HttpHeaders())
        val response: ResponseEntity<PaymentDTO> = restTemplate.exchange("/api/payments/123", HttpMethod.PUT, request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(firstPayment.asDTO())
    }

    @Test
    fun `When update payment then http status is NOT FOUND`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.empty()

        val request = HttpEntity<PaymentDTO>(firstPayment.asDTO(), HttpHeaders())
        val response: ResponseEntity<Any> = restTemplate.exchange("/api/payments/123", HttpMethod.PUT, request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `When update payment with zero or negative amount then htp status is BadRequest`() {
        val request = HttpEntity<PaymentDTO>(paymentWithInvalidAMount, HttpHeaders())
        val response: ResponseEntity<Any> = restTemplate.exchange("/api/payments/123", HttpMethod.PUT, request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `When delete payment then http status is OK`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.of(firstPayment)
        every { paymentRepository.delete(any<Payment>()) } returns Unit

        val response: ResponseEntity<PaymentDTO> = restTemplate.exchange("/api/payments/123", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(firstPayment.asDTO())
    }

    @Test
    fun `When delete payment then http status is NOT FOUND`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.empty()

        val response: ResponseEntity<Any> = restTemplate.exchange("/api/payments/123", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
