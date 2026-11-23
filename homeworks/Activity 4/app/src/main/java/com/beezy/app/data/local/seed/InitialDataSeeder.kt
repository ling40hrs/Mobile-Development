package com.beezy.app.data.local.seed

import com.beezy.app.data.local.entity.CardEntity
import com.beezy.app.data.local.entity.DeckEntity
import com.beezy.app.data.local.entity.DeckWithCards

object InitialDataSeeder {

    fun getInitialDecks(): List<DeckWithCards> {
        val introDeck = DeckEntity(
            id = "deck-intro",
            title = "Getting Started with Beezy",
            description = "Welcome! Master the basics of creating decks, studying flashcards, and taking quizzes.",
            tags = listOf("Tutorial", "Beezy"),
            builtIn = true,
            starred = true,
            createdAt = 1710000000000L
        )

        val introCards = listOf(
            CardEntity(
                id = "card-intro-1",
                deckId = "deck-intro",
                question = "What is a Deck in Beezy?",
                answer = "A Deck is a collection of flashcards focused on a single topic, subject, or chapter. You can create as many decks as you need and tag them for quick access.",
                tag = "Basics",
                reps = 1,
                interval = 1,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis() + 86400000L
            ),
            CardEntity(
                id = "card-intro-2",
                deckId = "deck-intro",
                question = "How does Flashcard Mode work?",
                answer = "Flashcard mode tests your recall. Tap the card to flip it and reveal the answer, then rate how easily you recalled it (Again, Hard, Good, Easy) to schedule its next review.",
                tag = "Recall",
                reps = 0,
                interval = 1,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis()
            ),
            CardEntity(
                id = "card-intro-3",
                deckId = "deck-intro",
                question = "What is Quiz Mode?",
                answer = "Quiz mode automatically turns your flashcard deck into a multiple-choice quiz, generating smart distractors from your other cards to test your recognition under pressure.",
                tag = "Quiz",
                reps = 2,
                interval = 6,
                easeFactor = 2.6,
                dueDate = System.currentTimeMillis() + (6 * 86400000L)
            )
        )

        val tipsDeck = DeckEntity(
            id = "deck-tips",
            title = "Study Tips & Techniques",
            description = "Evidence-based learning strategies backed by cognitive science and psychology.",
            tags = listOf("Study Skills", "Science"),
            builtIn = true,
            starred = false,
            createdAt = 1710000010000L
        )

        val tipsCards = listOf(
            CardEntity(
                id = "card-tips-1",
                deckId = "deck-tips",
                question = "What is the Pomodoro Technique?",
                answer = "A time-management method that breaks work into 25-minute focused intervals followed by 5-minute short breaks. After 4 intervals, take a longer 15–30 minute break.",
                tag = "Pomodoro",
                reps = 3,
                interval = 24, // mature card!
                easeFactor = 2.7,
                dueDate = System.currentTimeMillis() + (24 * 86400000L)
            ),
            CardEntity(
                id = "card-tips-2",
                deckId = "deck-tips",
                question = "What is Spaced Repetition?",
                answer = "A learning technique where reviews are scheduled at increasing intervals. Cards you find difficult appear frequently, while mastered cards appear weeks or months apart.",
                tag = "Memory",
                reps = 3,
                interval = 22, // mature card!
                easeFactor = 2.6,
                dueDate = System.currentTimeMillis() + (22 * 86400000L)
            ),
            CardEntity(
                id = "card-tips-3",
                deckId = "deck-tips",
                question = "What is Active Recall (The Testing Effect)?",
                answer = "Actively retrieving information from memory strengthens neural pathways far more effectively than passive re-reading or highlighting notes.",
                tag = "Recall",
                reps = 1,
                interval = 3,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis() + (3 * 86400000L)
            ),
            CardEntity(
                id = "card-tips-4",
                deckId = "deck-tips",
                question = "What is the Feynman Technique?",
                answer = "Explain a concept in simple, jargon-free language as if teaching it to a child. Any gaps or stumbling in your explanation pinpoint exactly what you need to review.",
                tag = "Understanding",
                reps = 0,
                interval = 1,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis()
            ),
            CardEntity(
                id = "card-tips-5",
                deckId = "deck-tips",
                question = "What is Interleaving?",
                answer = "Mixing different topics or problem types within a single study session instead of blocking one topic for hours. This improves categorization and problem-solving transfer.",
                tag = "Strategy",
                reps = 2,
                interval = 6,
                easeFactor = 2.5,
                dueDate = System.currentTimeMillis() + (6 * 86400000L)
            ),
            CardEntity(
                id = "card-tips-6",
                deckId = "deck-tips",
                question = "Why is Sleep essential for Learning?",
                answer = "During slow-wave and REM sleep, the hippocampus replays and transfers learned facts into the neocortex for long-term consolidation. Lack of sleep impairs memory formation.",
                tag = "Biology",
                reps = 1,
                interval = 1,
                easeFactor = 2.4,
                dueDate = System.currentTimeMillis() + 86400000L
            )
        )

        return listOf(
            DeckWithCards(deck = introDeck, cards = introCards),
            DeckWithCards(deck = tipsDeck, cards = tipsCards)
        )
    }
}
