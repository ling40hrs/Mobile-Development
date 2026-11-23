package com.beezy.app.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Decks : Screen("decks")
    object Study : Screen("study/{deckId}") {
        fun createRoute(deckId: String) = "study/$deckId"
    }
    object Flashcards : Screen("flashcards/{deckId}") {
        fun createRoute(deckId: String) = "flashcards/$deckId"
    }
    object Quiz : Screen("quiz/{deckId}") {
        fun createRoute(deckId: String) = "quiz/$deckId"
    }
    object Pomodoro : Screen("pomodoro")
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
    object TermsOfService : Screen("terms_of_service")
}
