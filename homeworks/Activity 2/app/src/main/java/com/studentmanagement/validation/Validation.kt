package com.studentmanagement.validation

data class FieldError(val field: String, val messageRes: Int)

sealed interface Validation<out T> {

    val errors: List<FieldError>

    data class Success<T>(val value: T) : Validation<T> {
        override val errors: List<FieldError> = emptyList()
    }

    data class Failure(override val errors: List<FieldError>) : Validation<Nothing> {

        constructor(field: String, messageRes: Int) : this(listOf(FieldError(field, messageRes)))
    }
}

fun <T> Validation<T>.errorFor(field: String): FieldError? = errors.firstOrNull { it.field == field }

val <T> Validation<T>.firstError: FieldError?
    get() = errors.firstOrNull()

val <T> Validation<T>.isValid: Boolean
    get() = this is Validation.Success
