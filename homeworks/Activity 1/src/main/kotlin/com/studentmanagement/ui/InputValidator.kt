package com.studentmanagement.ui

import java.util.Scanner

class InputValidator(private val scanner: Scanner = Scanner(System.`in`)) {

    fun readNonEmptyString(prompt: String): String {
        while (true) {
            print(prompt)
            if (!scanner.hasNextLine()) {
                throw NoSuchElementException("Input stream closed.")
            }
            val input = scanner.nextLine().trim()
            if (input.isNotEmpty()) {
                return input
            }
            println("  [!] Input cannot be blank. Please try again.")
        }
    }

    fun readOptionalString(prompt: String): String? {
        print(prompt)
        if (!scanner.hasNextLine()) return null
        val input = scanner.nextLine().trim()
        return if (input.isEmpty()) null else input
    }

    fun readInt(prompt: String, min: Int, max: Int): Int {
        while (true) {
            print(prompt)
            if (!scanner.hasNextLine()) {
                throw NoSuchElementException("Input stream closed.")
            }
            val input = scanner.nextLine().trim()
            try {
                val value = input.toInt()
                if (value in min..max) {
                    return value
                } else {
                    println("  [!] Please enter a number between $min and $max.")
                }
            } catch (e: NumberFormatException) {
                println("  [!] Invalid numeric format. Please enter a valid integer.")
            }
        }
    }

    fun readDouble(prompt: String, min: Double, max: Double): Double {
        while (true) {
            print(prompt)
            if (!scanner.hasNextLine()) {
                throw NoSuchElementException("Input stream closed.")
            }
            val input = scanner.nextLine().trim()
            try {
                val value = input.toDouble()
                if (value in min..max) {
                    return value
                } else {
                    println("  [!] Please enter a value between %.2f and %.2f.".format(min, max))
                }
            } catch (e: NumberFormatException) {
                println("  [!] Invalid decimal format. Please enter a valid number (e.g. 1.75).")
            }
        }
    }

    fun readGrade(prompt: String): Double {
        while (true) {
            val value = readDouble(prompt, 0.00, 4.00)
            if (value == 0.0 || value >= 1.00) return value
            println("  [!] Grade must be 0.00 (Failed) or between 1.00 and 4.00.")
        }
    }

    fun readConfirmation(prompt: String, defaultIsYes: Boolean = false): Boolean {
        val hint = if (defaultIsYes) "[Y/n]" else "[y/N]"
        print("$prompt $hint: ")
        if (!scanner.hasNextLine()) return defaultIsYes

        val input = scanner.nextLine().trim().lowercase()
        return when {
            input.isEmpty() -> defaultIsYes
            input == "y" || input == "yes" -> true
            input == "n" || input == "no" -> false
            else -> {
                println("  [!] Unrecognized response, defaulting to ${if (defaultIsYes) "Yes" else "No"}.")
                defaultIsYes
            }
        }
    }

    fun pressEnterToContinue() {
        print("Press Enter to continue...")
        if (scanner.hasNextLine()) {
            scanner.nextLine()
        }
    }
}
