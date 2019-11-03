package com.github.pdraguiar.kotlincrud.extension

import com.github.pdraguiar.kotlincrud.model.Recipient
import com.github.pdraguiar.kotlincrud.model.dto.RecipientDTO

fun RecipientDTO.asEntity(): Recipient = Recipient(code, name)