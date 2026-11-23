package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.SM2Result
import kotlin.math.roundToInt

/**
 * Executes the SuperMemo SM-2 spaced repetition algorithm.
 *
 * Grades:
 * 1: Again (Complete blackout, reset repetitions)
 * 2: Hard (Correct response with difficulty)
 * 3: Good (Correct response after brief hesitation)
 * 4: Easy (Effortless recall)
 */
class CalculateSM2UseCase {

    operator fun invoke(
        currentReps: Int,
        currentInterval: Int,
        currentEaseFactor: Double,
        grade: Int
    ): SM2Result {
        // Map 1..4 user grade to SM-2 quality score q in 0..5
        val q = when (grade) {
            1 -> 0 // Again
            2 -> 3 // Hard
            3 -> 4 // Good
            4 -> 5 // Easy
            else -> 3
        }

        var reps = currentReps
        var interval = currentInterval
        var easeFactor = currentEaseFactor

        if (q < 3) {
            // Failed recall: reset repetitions to 0 and interval to 1 day
            reps = 0
            interval = 1
        } else {
            // Successful recall: scale interval based on repetitions and ease factor
            interval = when (reps) {
                0 -> 1
                1 -> 6
                else -> (interval * easeFactor).roundToInt()
            }
            reps += 1
        }

        // SM-2 Ease Factor calculation: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        easeFactor += (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        if (easeFactor < 1.3) easeFactor = 1.3 // Hard minimum threshold

        val oneDayMillis = 86_400_000L
        val dueDate = System.currentTimeMillis() + (interval * oneDayMillis)

        return SM2Result(
            reps = reps,
            interval = interval,
            easeFactor = easeFactor,
            dueDate = dueDate
        )
    }
}
