package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.Card
import com.beezy.app.domain.model.QuizQuestion

class GenerateQuizUseCase {

    operator fun invoke(
        deckCards: List<Card>,
        fallbackPoolCards: List<Card> = emptyList()
    ): List<QuizQuestion> {
        if (deckCards.isEmpty()) return emptyList()

        val allPoolAnswers = (deckCards.map { it.answer } + fallbackPoolCards.map { it.answer })
            .distinct()

        val shuffledCards = deckCards.shuffled()

        return shuffledCards.map { card ->
            val correctAnswer = card.answer

            // Gather candidate distractors from same deck first
            val sameDeckDistractors = deckCards
                .map { it.answer }
                .filter { it != correctAnswer }
                .distinct()
                .shuffled()

            val neededDistractors = 3
            val selectedDistractors = mutableListOf<String>()

            selectedDistractors.addAll(sameDeckDistractors.take(neededDistractors))

            // If not enough distractors in the same deck, pull from fallback pool
            if (selectedDistractors.size < neededDistractors) {
                val remainingCount = neededDistractors - selectedDistractors.size
                val poolDistractors = allPoolAnswers
                    .filter { it != correctAnswer && !selectedDistractors.contains(it) }
                    .shuffled()
                    .take(remainingCount)
                selectedDistractors.addAll(poolDistractors)
            }

            // If still fewer than 3 (e.g. brand new app with 1 card), create synthetic plausible options
            while (selectedDistractors.size < 3) {
                selectedDistractors.add("Alternative definition ${selectedDistractors.size + 1}")
            }

            // Combine and shuffle options
            val combinedOptions = (selectedDistractors.take(3) + correctAnswer).shuffled()
            val correctIndex = combinedOptions.indexOf(correctAnswer)

            QuizQuestion(
                cardId = card.id,
                question = card.question,
                options = combinedOptions,
                correctIndex = correctIndex
            )
        }
    }
}
