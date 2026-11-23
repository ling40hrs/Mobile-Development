package com.beezy.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateSM2UseCaseTest {

    private lateinit var useCase: CalculateSM2UseCase

    @Before
    fun setUp() {
        useCase = CalculateSM2UseCase()
    }

    @Test
    fun testFirstReview_gradeGood_intervalBecomes1AndRepsIncrements() {
        val result = useCase(
            currentReps = 0,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            grade = 3 // Good
        )

        assertEquals(1, result.reps)
        assertEquals(1, result.interval)
        assertTrue("Ease factor should remain around 2.5", result.easeFactor >= 2.4)
    }

    @Test
    fun testSecondReview_gradeGood_intervalBecomes6() {
        val result = useCase(
            currentReps = 1,
            currentInterval = 1,
            currentEaseFactor = 2.5,
            grade = 3 // Good
        )

        assertEquals(2, result.reps)
        assertEquals(6, result.interval)
    }

    @Test
    fun testThirdReview_gradeEasy_intervalScalesWithEaseFactor() {
        val result = useCase(
            currentReps = 2,
            currentInterval = 6,
            currentEaseFactor = 2.5,
            grade = 4 // Easy
        )

        assertEquals(3, result.reps)
        assertTrue("Interval should scale (approx 15 days)", result.interval >= 15)
        assertTrue("Ease factor should increase for Easy rating", result.easeFactor > 2.5)
    }

    @Test
    fun testFailedRecall_gradeAgain_resetsRepetitionsToZeroAndIntervalToOne() {
        val result = useCase(
            currentReps = 5,
            currentInterval = 30,
            currentEaseFactor = 2.4,
            grade = 1 // Again (Failed)
        )

        assertEquals(0, result.reps)
        assertEquals(1, result.interval)
    }

    @Test
    fun testEaseFactor_neverDropsBelowMinimumThreshold() {
        var currentEase = 1.35
        for (i in 1..10) {
            val result = useCase(
                currentReps = 0,
                currentInterval = 1,
                currentEaseFactor = currentEase,
                grade = 1 // Repeated failures
            )
            currentEase = result.easeFactor
        }

        assertTrue("Ease factor must never fall below 1.3", currentEase >= 1.3)
    }
}
