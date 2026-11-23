package com.beezy.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beezy.app.di.AppContainer
import com.beezy.app.presentation.about.AVATAR_OPTIONS
import com.beezy.app.presentation.about.AboutScreen
import com.beezy.app.presentation.about.AboutViewModel
import com.beezy.app.presentation.about.static.PrivacyPolicyScreen
import com.beezy.app.presentation.about.static.TermsOfServiceScreen
import com.beezy.app.presentation.auth.AuthScreen
import com.beezy.app.presentation.auth.AuthViewModel
import com.beezy.app.presentation.components.BeezyBottomBar
import com.beezy.app.presentation.components.BeezyTopAppBar
import com.beezy.app.presentation.decks.DecksScreen
import com.beezy.app.presentation.decks.DecksViewModel
import com.beezy.app.presentation.flashcard.FlashcardScreen
import com.beezy.app.presentation.flashcard.FlashcardViewModel
import com.beezy.app.presentation.pomodoro.PomodoroScreen
import com.beezy.app.presentation.pomodoro.PomodoroViewModel
import com.beezy.app.presentation.quiz.QuizScreen
import com.beezy.app.presentation.quiz.QuizViewModel
import com.beezy.app.presentation.study.DeckStudyScreen
import com.beezy.app.presentation.study.DeckStudyViewModel
import com.beezy.app.presentation.theme.BeezyTheme
import com.beezy.app.presentation.theme.PillShape

@Composable
fun BeezyNavGraph(
    appContainer: AppContainer,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Create persistent ViewModels from AppContainer
    val decksViewModel = remember {
        DecksViewModel(
            deckRepository = appContainer.deckRepository,
            cardRepository = appContainer.cardRepository,
            extractCardsUseCase = appContainer.extractCardsFromDocumentUseCase
        )
    }

    val pomodoroViewModel = remember {
        PomodoroViewModel(
            context = appContainer.context,
            preferencesRepository = appContainer.preferencesRepository
        )
    }

    val aboutViewModel = remember {
        AboutViewModel(
            preferencesRepository = appContainer.preferencesRepository,
            deckRepository = appContainer.deckRepository,
            authRepository = appContainer.authRepository
        )
    }

    val authViewModel = remember {
        AuthViewModel(
            authRepository = appContainer.authRepository
        )
    }

    val allDecks by decksViewModel.uiState.collectAsState()

    // Show Bottom Bar only on root navigation tabs
    val showBottomBar = currentRoute in listOf(
        Screen.Decks.route,
        Screen.Pomodoro.route,
        Screen.About.route
    ) || (currentRoute?.startsWith("study") == true)

    // Show Top Bar on root screens
    val showTopBar = currentRoute in listOf(
        Screen.Decks.route,
        Screen.Pomodoro.route,
        Screen.About.route
    )

    val topBarTitle = when (currentRoute) {
        Screen.Decks.route -> "Decks"
        Screen.Pomodoro.route -> "Focus Timer"
        Screen.About.route -> "About Beezy"
        else -> "Beezy"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BeezyTheme.colors.bg,
        topBar = {
            if (showTopBar) {
                val currentUser by appContainer.authRepository.currentUserFlow.collectAsState(initial = null)
                BeezyTopAppBar(
                    title = topBarTitle,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    actions = {
                        if (currentUser != null) {
                            val user = currentUser!!
                            val avatarEmoji = AVATAR_OPTIONS.getOrElse(user.avatarIndex) { AVATAR_OPTIONS[0] }.first
                            Box(
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(BeezyTheme.colors.surface2)
                                    .clickable { navController.navigate(Screen.About.route) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = avatarEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = user.displayName.take(10),
                                        color = BeezyTheme.colors.textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(BeezyTheme.colors.accent.copy(alpha = 0.15f))
                                    .clickable { navController.navigate(Screen.Auth.route) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign In",
                                    color = BeezyTheme.colors.accent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BeezyBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        if (targetRoute == Screen.Study.route) {
                            val targetDeckId = allDecks.decks.firstOrNull()?.id
                            if (targetDeckId != null) {
                                navController.navigate(Screen.Study.createRoute(targetDeckId)) {
                                    popUpTo(Screen.Decks.route)
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(Screen.Decks.route) {
                                    popUpTo(Screen.Decks.route)
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Decks.route)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Decks.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 0. Authentication (Login / Sign Up)
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = { navController.popBackStack() },
                    onContinueAsGuest = { navController.popBackStack() }
                )
            }

            // 1. Decks Screen
            composable(Screen.Decks.route) {
                DecksScreen(
                    viewModel = decksViewModel,
                    onNavigateToDeck = { deckId ->
                        navController.navigate(Screen.Study.createRoute(deckId))
                    }
                )
            }

            // 2. Deck Study / Detail Screen
            composable(
                route = Screen.Study.route,
                arguments = listOf(navArgument("deckId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: ""
                val studyViewModel = remember(deckId) {
                    DeckStudyViewModel(
                        deckId = deckId,
                        deckRepository = appContainer.deckRepository,
                        cardRepository = appContainer.cardRepository
                    )
                }

                DeckStudyScreen(
                    viewModel = studyViewModel,
                    onLaunchFlashcards = { id ->
                        navController.navigate(Screen.Flashcards.createRoute(id))
                    },
                    onLaunchQuiz = { id ->
                        navController.navigate(Screen.Quiz.createRoute(id))
                    }
                )
            }

            // 3. Flashcards Study Mode
            composable(
                route = Screen.Flashcards.route,
                arguments = listOf(navArgument("deckId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: ""
                val flashcardViewModel = remember(deckId) {
                    FlashcardViewModel(
                        deckId = deckId,
                        deckRepository = appContainer.deckRepository,
                        cardRepository = appContainer.cardRepository,
                        calculateSM2UseCase = appContainer.calculateSM2UseCase,
                        calculateMaturityUseCase = appContainer.calculateMaturityUseCase
                    )
                }

                FlashcardScreen(
                    viewModel = flashcardViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 4. Quiz Mode
            composable(
                route = Screen.Quiz.route,
                arguments = listOf(navArgument("deckId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: ""
                val quizViewModel = remember(deckId) {
                    QuizViewModel(
                        deckId = deckId,
                        deckRepository = appContainer.deckRepository,
                        cardRepository = appContainer.cardRepository,
                        generateQuizUseCase = appContainer.generateQuizUseCase
                    )
                }

                QuizScreen(
                    viewModel = quizViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 5. Pomodoro Focus Timer
            composable(Screen.Pomodoro.route) {
                PomodoroScreen(viewModel = pomodoroViewModel)
            }

            // 6. About & Settings
            composable(Screen.About.route) {
                AboutScreen(
                    viewModel = aboutViewModel,
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onNavigateToTerms = { navController.navigate(Screen.TermsOfService.route) }
                )
            }

            // 7. Privacy Policy
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
            }

            // 8. Terms of Service
            composable(Screen.TermsOfService.route) {
                TermsOfServiceScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
