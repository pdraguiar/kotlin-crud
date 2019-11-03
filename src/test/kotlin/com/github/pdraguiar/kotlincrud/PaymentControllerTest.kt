package com.github.pdraguiar.kotlincrud

import com.github.pdraguiar.kotlincrud.extension.asDTO
import com.github.pdraguiar.kotlincrud.model.Payment
import com.github.pdraguiar.kotlincrud.model.Recipient
import com.github.pdraguiar.kotlincrud.model.dto.PaymentDTO
import com.github.pdraguiar.kotlincrud.model.dto.PaymentListDTO
import com.github.pdraguiar.kotlincrud.model.dto.RecipientDTO
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
import org.springframework.data.domain.Pageable
import org.springframework.http.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @MockkBean
    private lateinit var paymentRepository: PaymentRepository

    lateinit var payment: Payment
    lateinit var paymentWithInvalidAMount: PaymentDTO
    var payments: MutableList<Payment> = mutableListOf<Payment>()
    var paymentsSize: Long = 0L

    @BeforeAll
    fun init() {
        payment = Payment(description = "some description",
                amount = BigDecimal(100.00),
                expiresAt = LocalDate.parse("2019-10-30"),
                recipient = Recipient(1, "A Recipient inc."),
                id = UUID.randomUUID().toString(),
                paidAt = LocalDate.parse("2019-10-30").atStartOfDay())

        for (i in 0..9) {
            payments.add(
                Payment(description = "some description $i",
                        amount = BigDecimal((i + 59.90) * 10),
                        expiresAt = LocalDate.now().plusDays(i.toLong()),
                        recipient = Recipient(i, "A Recipient $i inc."),
                        id = UUID.randomUUID().toString(),
                        paidAt = LocalDate.now().plusDays(i.toLong()).atStartOfDay()))
        }

        paymentsSize = payments.size.toLong()

        paymentWithInvalidAMount = PaymentDTO(description = "a description",
                amount = BigDecimal(-1),
                expiresAt = LocalDate.parse("2019-10-05"),
                recipient = RecipientDTO(1, "A Recipient inc."),
                id = UUID.randomUUID().toString(),
                paidAt = LocalDate.parse("2019-10-31").atStartOfDay())
    }

    @Test
    fun `When list payments without page and size then return 10 payments`() {
        every { paymentRepository.findAll(any<Pageable>()).content } returns payments
        every { paymentRepository.count() } returns paymentsSize

        val response = restTemplate.getForEntity<PaymentListDTO>("/api/payments")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.payments?.size).isEqualTo(paymentsSize)
        assertThat(response.body?.total).isEqualTo(paymentsSize)
    }

    @Test
    fun `When list payments with page of 5 elements then return just 5 elements`() {
        every { paymentRepository.findAll(any<Pageable>()).content } returns payments.subList(0, 4)
        every { paymentRepository.count() } returns paymentsSize

        val response = restTemplate.getForEntity<PaymentListDTO>("/api/payments?page=0&size=5")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.payments?.size).isEqualTo(payments.subList(0, 4).size)
        assertThat(response.body?.total).isEqualTo(paymentsSize)
    }

    @Test
    fun `When list payments with negative page parameter then http status is BAD_REQUEST`() {
        val response = restTemplate.getForEntity<Any>("/api/payments?page=-1")

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `When list payments with not positive size parameter then http status is BAD_REQUEST`() {
        val response = restTemplate.getForEntity<Any>("/api/payments?size=-1")

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `When create payment then http status is CREATED`() {
        every { paymentRepository.save(any<Payment>()) } returns payment

        val response = restTemplate.postForEntity<PaymentDTO>("/api/payments", payment.asDTO())

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isEqualTo(payment.asDTO())
    }

    @Test
    fun `When create payment with zero or negative amount then http status is BadRequest`() {
        val response = restTemplate.postForEntity<Any>("/api/payments", paymentWithInvalidAMount)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `When update payment then http status is OK`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.of(payment)
        every { paymentRepository.save(any<Payment>()) } returns payment

        val request = HttpEntity<PaymentDTO>(payment.asDTO(), HttpHeaders())
        val response: ResponseEntity<PaymentDTO> = restTemplate.exchange("/api/payments/123", HttpMethod.PUT, request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(payment.asDTO())
    }

    @Test
    fun `When update payment then http status is NOT FOUND`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.empty()

        val request = HttpEntity<PaymentDTO>(payment.asDTO(), HttpHeaders())
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
        every { paymentRepository.findById(any<String>()) } returns Optional.of(payment)
        every { paymentRepository.delete(any<Payment>()) } returns Unit

        val response: ResponseEntity<PaymentDTO> = restTemplate.exchange("/api/payments/123", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(payment.asDTO())
    }

    @Test
    fun `When delete payment then http status is NOT FOUND`() {
        every { paymentRepository.findById(any<String>()) } returns Optional.empty()

        val response: ResponseEntity<Any> = restTemplate.exchange("/api/payments/123", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
