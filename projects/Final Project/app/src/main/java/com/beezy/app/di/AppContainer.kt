package com.beezy.app.di

import android.content.Context
import com.beezy.app.data.local.BeezyDatabase
import com.beezy.app.data.local.datastore.UserPreferencesRepository
import com.beezy.app.data.repository.AuthRepositoryImpl
import com.beezy.app.data.repository.CardRepositoryImpl
import com.beezy.app.data.repository.DeckRepositoryImpl
import com.beezy.app.domain.repository.AuthRepository
import com.beezy.app.domain.repository.CardRepository
import com.beezy.app.domain.repository.DeckRepository
import com.beezy.app.domain.usecase.CalculateMaturityUseCase
import com.beezy.app.domain.usecase.CalculateSM2UseCase
import com.beezy.app.domain.usecase.ExtractCardsFromDocumentUseCase
import com.beezy.app.domain.usecase.GenerateQuizUseCase

interface AppContainer {
    val context: Context
    val dispatchers: DispatcherProvider
    val database: BeezyDatabase
    val deckRepository: DeckRepository
    val cardRepository: CardRepository
    val preferencesRepository: UserPreferencesRepository
    val authRepository: AuthRepository
    val calculateSM2UseCase: CalculateSM2UseCase
    val calculateMaturityUseCase: CalculateMaturityUseCase
    val generateQuizUseCase: GenerateQuizUseCase
    val extractCardsFromDocumentUseCase: ExtractCardsFromDocumentUseCase
}

class DefaultAppContainer(override val context: Context) : AppContainer {
    override val dispatchers: DispatcherProvider by lazy {
        StandardDispatcherProvider()
    }

    override val database: BeezyDatabase by lazy {
        BeezyDatabase(context, dispatchers.io)
    }

    override val deckRepository: DeckRepository by lazy {
        DeckRepositoryImpl(database)
    }

    override val cardRepository: CardRepository by lazy {
        CardRepositoryImpl(database)
    }

    override val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database, preferencesRepository)
    }

    override val calculateSM2UseCase: CalculateSM2UseCase by lazy {
        CalculateSM2UseCase()
    }

    override val calculateMaturityUseCase: CalculateMaturityUseCase by lazy {
        CalculateMaturityUseCase()
    }

    override val generateQuizUseCase: GenerateQuizUseCase by lazy {
        GenerateQuizUseCase()
    }

    override val extractCardsFromDocumentUseCase: ExtractCardsFromDocumentUseCase by lazy {
        ExtractCardsFromDocumentUseCase()
    }
}
