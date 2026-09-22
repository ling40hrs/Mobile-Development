package com.studentmanagement.ui

import java.util.Scanner

/**
 * Robust input reader and validation utility for CLI interactions.
 */
class InputValidator(private val scanner: Scanner = Scanner(System.`in`)) {

    /**
     * Reads a non-empty string from console.
     */
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

    /**
     * Reads an optional string (returns null if user presses enter directly).
     */
    fun readOptionalString(prompt: String): String? {
        print(prompt)
        if (!scanner.hasNextLine()) return null
        val input = scanner.nextLine().trim()
        return if (input.isEmpty()) null else input
    }

    /**
     * Reads an integer within the specified inclusive range.
     */
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

    /**
     * Reads a double value within the specified inclusive range.
     */
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

    /**
     * Prompts the user with a Yes/No confirmation.
     */
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

    /**
     * Pauses and waits for user to press Enter before returning to menu.
     */
    fun pressEnterToContinue() {
        print("Press Enter to continue...")
        if (scanner.hasNextLine()) {
            scanner.nextLine()
        }
    }
}
