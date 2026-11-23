package com.beezy.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.beezy.app.data.local.dao.CardDao
import com.beezy.app.data.local.dao.DeckDao
import com.beezy.app.data.local.dao.UserDao
import com.beezy.app.data.local.entity.CardEntity
import com.beezy.app.data.local.entity.DeckEntity
import com.beezy.app.data.local.entity.DeckWithCards
import com.beezy.app.data.local.entity.UserEntity
import com.beezy.app.data.local.seed.InitialDataSeeder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BeezyDatabase(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val _decksFlow = MutableStateFlow<List<DeckWithCards>>(emptyList())
    val decksFlow: Flow<List<DeckWithCards>> = _decksFlow.asStateFlow()

    init {
        // Initial load of decks
        refreshDecksSync()
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE decks (
                id TEXT PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                tags TEXT NOT NULL,
                builtIn INTEGER NOT NULL DEFAULT 0,
                starred INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE cards (
                id TEXT PRIMARY KEY NOT NULL,
                deckId TEXT NOT NULL,
                question TEXT NOT NULL,
                answer TEXT NOT NULL,
                tag TEXT,
                reps INTEGER NOT NULL DEFAULT 0,
                interval INTEGER NOT NULL DEFAULT 1,
                easeFactor REAL NOT NULL DEFAULT 2.5,
                dueDate INTEGER,
                lastReviewed INTEGER,
                FOREIGN KEY (deckId) REFERENCES decks (id) ON DELETE CASCADE
            );
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX idx_cards_deckId ON cards (deckId);")

        // Users table for local account management (Login / Signup)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY NOT NULL,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                passwordHash TEXT NOT NULL,
                displayName TEXT NOT NULL,
                avatarIndex INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL
            );
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);")

        // Prepopulate with default decks and demo student account
        seedInitialData(db)
        seedDemoUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    passwordHash TEXT NOT NULL,
                    displayName TEXT NOT NULL,
                    avatarIndex INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL
                );
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);")
            seedDemoUser(db)
        }
    }

    private fun seedDemoUser(db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put("id", "user-demo")
            put("username", "demo")
            put("email", "demo@beezy.app")
            put("passwordHash", hashPassword("password123"))
            put("displayName", "Beezy Student")
            put("avatarIndex", 1) // Wise Owl 🦉
            put("createdAt", System.currentTimeMillis())
        }
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val initialData = InitialDataSeeder.getInitialDecks()
        for (item in initialData) {
            val deckValues = ContentValues().apply {
                put("id", item.deck.id)
                put("title", item.deck.title)
                put("description", item.deck.description)
                put("tags", item.deck.tags.joinToString(","))
                put("builtIn", if (item.deck.builtIn) 1 else 0)
                put("starred", if (item.deck.starred) 1 else 0)
                put("createdAt", item.deck.createdAt)
            }
            db.insert("decks", null, deckValues)

            for (card in item.cards) {
                val cardValues = ContentValues().apply {
                    put("id", card.id)
                    put("deckId", card.deckId)
                    put("question", card.question)
                    put("answer", card.answer)
                    put("tag", card.tag)
                    put("reps", card.reps)
                    put("interval", card.interval)
                    put("easeFactor", card.easeFactor)
                    put("dueDate", card.dueDate)
                    put("lastReviewed", card.lastReviewed)
                }
                db.insert("cards", null, cardValues)
            }
        }
    }

    @Synchronized
    private fun refreshDecksSync() {
        val db = readableDatabase
        val list = mutableListOf<DeckWithCards>()
        
        val deckCursor = db.rawQuery(
            "SELECT id, title, description, tags, builtIn, starred, createdAt FROM decks ORDER BY createdAt DESC",
            null
        )
        deckCursor.use { cursor ->
            while (cursor.moveToNext()) {
                val deckId = cursor.getString(0)
                val deck = DeckEntity(
                    id = deckId,
                    title = cursor.getString(1),
                    description = cursor.getString(2),
                    tags = cursor.getString(3).split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    builtIn = cursor.getInt(4) == 1,
                    starred = cursor.getInt(5) == 1,
                    createdAt = cursor.getLong(6)
                )

                // Fetch cards for this deck
                val cards = getCardsForDeckSync(deckId)
                list.add(DeckWithCards(deck = deck, cards = cards))
            }
        }
        _decksFlow.value = list
    }

    private fun getCardsForDeckSync(deckId: String): List<CardEntity> {
        val db = readableDatabase
        val cards = mutableListOf<CardEntity>()
        val cursor = db.rawQuery(
            """
            SELECT id, deckId, question, answer, tag, reps, interval, easeFactor, dueDate, lastReviewed 
            FROM cards WHERE deckId = ?
            """.trimIndent(),
            arrayOf(deckId)
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                cards.add(
                    CardEntity(
                        id = c.getString(0),
                        deckId = c.getString(1),
                        question = c.getString(2),
                        answer = c.getString(3),
                        tag = c.getString(4),
                        reps = c.getInt(5),
                        interval = c.getInt(6),
                        easeFactor = c.getDouble(7),
                        dueDate = if (c.isNull(8)) null else c.getLong(8),
                        lastReviewed = if (c.isNull(9)) null else c.getLong(9)
                    )
                )
            }
        }
        return cards
    }

    suspend fun resetDatabase() = withContext(ioDispatcher) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("cards", null, null)
            db.delete("decks", null, null)
            seedInitialData(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        refreshDecksSync()
    }

    // --- DAO Implementations ---

    val deckDao = object : DeckDao {
        override fun getAllDecksFlow(): Flow<List<DeckWithCards>> = decksFlow

        override suspend fun getDeckById(id: String): DeckWithCards? = withContext(ioDispatcher) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT id, title, description, tags, builtIn, starred, createdAt FROM decks WHERE id = ?",
                arrayOf(id)
            )
            cursor.use { c ->
                if (c.moveToNext()) {
                    val deck = DeckEntity(
                        id = c.getString(0),
                        title = c.getString(1),
                        description = c.getString(2),
                        tags = c.getString(3).split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        builtIn = c.getInt(4) == 1,
                        starred = c.getInt(5) == 1,
                        createdAt = c.getLong(6)
                    )
                    val cards = getCardsForDeckSync(id)
                    return@withContext DeckWithCards(deck, cards)
                }
            }
            null
        }

        override suspend fun insertDeck(deck: DeckEntity) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("id", deck.id)
                put("title", deck.title)
                put("description", deck.description)
                put("tags", deck.tags.joinToString(","))
                put("builtIn", if (deck.builtIn) 1 else 0)
                put("starred", if (deck.starred) 1 else 0)
                put("createdAt", deck.createdAt)
            }
            db.insertWithOnConflict("decks", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshDecksSync()
        }

        override suspend fun updateDeck(deck: DeckEntity) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("title", deck.title)
                put("description", deck.description)
                put("tags", deck.tags.joinToString(","))
            }
            db.update("decks", values, "id = ?", arrayOf(deck.id))
            refreshDecksSync()
        }

        override suspend fun toggleStar(id: String, isStarred: Boolean) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("starred", if (isStarred) 1 else 0)
            }
            db.update("decks", values, "id = ?", arrayOf(id))
            refreshDecksSync()
        }

        override suspend fun deleteDeck(id: String) = withContext(ioDispatcher) {
            val db = writableDatabase
            db.delete("decks", "id = ?", arrayOf(id))
            refreshDecksSync()
        }

        override suspend fun deleteAll() = withContext(ioDispatcher) {
            val db = writableDatabase
            db.delete("cards", null, null)
            db.delete("decks", null, null)
            refreshDecksSync()
        }
    }

    val cardDao = object : CardDao {
        override fun getCardsForDeckFlow(deckId: String): Flow<List<CardEntity>> {
            // Can be extracted via decksFlow
            return decksFlow.map { list ->
                list.find { it.deck.id == deckId }?.cards ?: emptyList()
            }
        }

        override suspend fun getCardsForDeck(deckId: String): List<CardEntity> = withContext(ioDispatcher) {
            getCardsForDeckSync(deckId)
        }

        override suspend fun getCardById(id: String): CardEntity? = withContext(ioDispatcher) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                """
                SELECT id, deckId, question, answer, tag, reps, interval, easeFactor, dueDate, lastReviewed 
                FROM cards WHERE id = ?
                """.trimIndent(),
                arrayOf(id)
            )
            cursor.use { c ->
                if (c.moveToNext()) {
                    return@withContext CardEntity(
                        id = c.getString(0),
                        deckId = c.getString(1),
                        question = c.getString(2),
                        answer = c.getString(3),
                        tag = c.getString(4),
                        reps = c.getInt(5),
                        interval = c.getInt(6),
                        easeFactor = c.getDouble(7),
                        dueDate = if (c.isNull(8)) null else c.getLong(8),
                        lastReviewed = if (c.isNull(9)) null else c.getLong(9)
                    )
                }
            }
            null
        }

        override suspend fun insertCard(card: CardEntity) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("id", card.id)
                put("deckId", card.deckId)
                put("question", card.question)
                put("answer", card.answer)
                put("tag", card.tag)
                put("reps", card.reps)
                put("interval", card.interval)
                put("easeFactor", card.easeFactor)
                put("dueDate", card.dueDate)
                put("lastReviewed", card.lastReviewed)
            }
            db.insertWithOnConflict("cards", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshDecksSync()
        }

        override suspend fun insertCards(cards: List<CardEntity>) = withContext(ioDispatcher) {
            val db = writableDatabase
            db.beginTransaction()
            try {
                for (card in cards) {
                    val values = ContentValues().apply {
                        put("id", card.id)
                        put("deckId", card.deckId)
                        put("question", card.question)
                        put("answer", card.answer)
                        put("tag", card.tag)
                        put("reps", card.reps)
                        put("interval", card.interval)
                        put("easeFactor", card.easeFactor)
                        put("dueDate", card.dueDate)
                        put("lastReviewed", card.lastReviewed)
                    }
                    db.insertWithOnConflict("cards", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            refreshDecksSync()
        }

        override suspend fun updateCard(card: CardEntity) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("question", card.question)
                put("answer", card.answer)
                put("tag", card.tag)
            }
            db.update("cards", values, "id = ?", arrayOf(card.id))
            refreshDecksSync()
        }

        override suspend fun updateSM2State(
            id: String,
            reps: Int,
            interval: Int,
            easeFactor: Double,
            dueDate: Long,
            lastReviewed: Long
        ) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("reps", reps)
                put("interval", interval)
                put("easeFactor", easeFactor)
                put("dueDate", dueDate)
                put("lastReviewed", lastReviewed)
            }
            db.update("cards", values, "id = ?", arrayOf(id))
            refreshDecksSync()
        }

        override suspend fun deleteCard(id: String) = withContext(ioDispatcher) {
            val db = writableDatabase
            db.delete("cards", "id = ?", arrayOf(id))
            refreshDecksSync()
        }

        override suspend fun resetDeckProgress(deckId: String) = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("reps", 0)
                put("interval", 1)
                put("easeFactor", 2.5)
                putNull("dueDate")
                putNull("lastReviewed")
            }
            db.update("cards", values, "deckId = ?", arrayOf(deckId))
            refreshDecksSync()
        }

        override suspend fun getAllCards(): List<CardEntity> = withContext(ioDispatcher) {
            val db = readableDatabase
            val cards = mutableListOf<CardEntity>()
            val cursor = db.rawQuery(
                "SELECT id, deckId, question, answer, tag, reps, interval, easeFactor, dueDate, lastReviewed FROM cards",
                null
            )
            cursor.use { c ->
                while (c.moveToNext()) {
                    cards.add(
                        CardEntity(
                            id = c.getString(0),
                            deckId = c.getString(1),
                            question = c.getString(2),
                            answer = c.getString(3),
                            tag = c.getString(4),
                            reps = c.getInt(5),
                            interval = c.getInt(6),
                            easeFactor = c.getDouble(7),
                            dueDate = if (c.isNull(8)) null else c.getLong(8),
                            lastReviewed = if (c.isNull(9)) null else c.getLong(9)
                        )
                    )
                }
            }
            cards
        }
    }

    val userDao = object : UserDao {
        override suspend fun insertUser(user: UserEntity): Long = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("id", user.id)
                put("username", user.username.trim().lowercase())
                put("email", user.email.trim().lowercase())
                put("passwordHash", user.passwordHash)
                put("displayName", user.displayName.trim())
                put("avatarIndex", user.avatarIndex)
                put("createdAt", user.createdAt)
            }
            db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_ABORT)
        }

        override suspend fun getUserByUsernameOrEmail(identifier: String): UserEntity? = withContext(ioDispatcher) {
            val db = readableDatabase
            val clean = identifier.trim().lowercase()
            val cursor = db.rawQuery(
                "SELECT id, username, email, passwordHash, displayName, avatarIndex, createdAt FROM users WHERE username = ? OR email = ? LIMIT 1",
                arrayOf(clean, clean)
            )
            cursor.use { c ->
                if (c.moveToNext()) {
                    return@withContext UserEntity(
                        id = c.getString(0),
                        username = c.getString(1),
                        email = c.getString(2),
                        passwordHash = c.getString(3),
                        displayName = c.getString(4),
                        avatarIndex = c.getInt(5),
                        createdAt = c.getLong(6)
                    )
                }
            }
            null
        }

        override suspend fun getUserById(id: String): UserEntity? = withContext(ioDispatcher) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT id, username, email, passwordHash, displayName, avatarIndex, createdAt FROM users WHERE id = ? LIMIT 1",
                arrayOf(id)
            )
            cursor.use { c ->
                if (c.moveToNext()) {
                    return@withContext UserEntity(
                        id = c.getString(0),
                        username = c.getString(1),
                        email = c.getString(2),
                        passwordHash = c.getString(3),
                        displayName = c.getString(4),
                        avatarIndex = c.getInt(5),
                        createdAt = c.getLong(6)
                    )
                }
            }
            null
        }

        override suspend fun updateUserProfile(id: String, displayName: String, avatarIndex: Int): Int = withContext(ioDispatcher) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("displayName", displayName.trim())
                put("avatarIndex", avatarIndex)
            }
            db.update("users", values, "id = ?", arrayOf(id))
        }

        override suspend fun deleteUser(id: String): Int = withContext(ioDispatcher) {
            val db = writableDatabase
            db.delete("users", "id = ?", arrayOf(id))
        }

        override suspend fun getAllUsers(): List<UserEntity> = withContext(ioDispatcher) {
            val db = readableDatabase
            val list = mutableListOf<UserEntity>()
            val cursor = db.rawQuery(
                "SELECT id, username, email, passwordHash, displayName, avatarIndex, createdAt FROM users ORDER BY createdAt ASC",
                null
            )
            cursor.use { c ->
                while (c.moveToNext()) {
                    list.add(
                        UserEntity(
                            id = c.getString(0),
                            username = c.getString(1),
                            email = c.getString(2),
                            passwordHash = c.getString(3),
                            displayName = c.getString(4),
                            avatarIndex = c.getInt(5),
                            createdAt = c.getLong(6)
                        )
                    )
                }
            }
            list
        }
    }

    companion object {
        const val DATABASE_NAME = "beezy.db"
        const val DATABASE_VERSION = 2

        fun hashPassword(password: String): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
