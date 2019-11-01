package com.github.pdraguiar.kotlincrud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories
@EnableMongoAuditing
@SpringBootApplication
class KotlinCrudApplication

fun main(args: Array<String>) {
	runApplication<KotlinCrudApplication>(*args)
}
