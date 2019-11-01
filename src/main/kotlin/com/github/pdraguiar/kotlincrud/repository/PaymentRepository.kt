package com.github.pdraguiar.kotlincrud.repository

import com.github.pdraguiar.kotlincrud.model.Payment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : MongoRepository<Payment, String> {
}