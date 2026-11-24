-- Beezy local database schema (version 3)
--
-- One SQLiteOpenHelper owns this file: app/src/main/java/com/beezy/app/data/local/BeezyDatabase.kt
-- The app creates the file on first launch and seeds the demo account plus two
-- starter decks. Apply this script to an empty database to reproduce the schema
-- without the seed data.
--
--   sqlite3 beezy.db < database/schema.sql
--
-- Foreign keys are enabled by the app through setForeignKeyConstraintsEnabled;
-- the PRAGMA below does the same for a standalone sqlite3 session.

PRAGMA foreign_keys = ON;

CREATE TABLE decks (
    id TEXT PRIMARY KEY NOT NULL,
    userId TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    tags TEXT NOT NULL,
    builtIn INTEGER NOT NULL DEFAULT 0,
    starred INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);

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

CREATE INDEX idx_cards_deckId ON cards (deckId);
CREATE INDEX idx_decks_userId ON decks (userId);

CREATE TABLE users (
    id TEXT PRIMARY KEY NOT NULL,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    passwordHash TEXT NOT NULL,
    displayName TEXT NOT NULL,
    avatarIndex INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);

CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
