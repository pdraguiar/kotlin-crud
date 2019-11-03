package com.github.pdraguiar.kotlincrud.model.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class RecipientDTO(@field:NotNull val code: Int,
                        @field:NotEmpty val name: String) {

}