package com.github.pdraguiar.kotlincrud.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class Payment(var description: String,
                   var amount: BigDecimal,
                   var expiresAt: LocalDate,
                   var recipient: Recipient,
                   @Id var id: String? = null,
                   var paidAt: LocalDateTime? = null,
                   @Version var version: Long? = null,
                   @CreatedDate var createdAt: LocalDateTime? = null,
                   @LastModifiedDate var updatedAt: LocalDateTime? = null)