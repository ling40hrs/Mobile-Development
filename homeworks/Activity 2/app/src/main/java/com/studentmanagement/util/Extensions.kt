package com.studentmanagement.util

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import java.util.Locale

fun EditText.value(): String = text.toString().trim()

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Double.toGradeText(): String = String.format(Locale.getDefault(), "%.1f", this)
