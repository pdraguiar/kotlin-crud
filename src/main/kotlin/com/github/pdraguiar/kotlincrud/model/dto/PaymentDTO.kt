package com.github.pdraguiar.kotlincrud.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentDTO(@field:NotEmpty val description: String,
                      @field:NotNull val amount: BigDecimal,
                      @field:NotNull val expiresAt: LocalDate,
                      val id: String? = null,
                      val paidAt: LocalDateTime? = null)