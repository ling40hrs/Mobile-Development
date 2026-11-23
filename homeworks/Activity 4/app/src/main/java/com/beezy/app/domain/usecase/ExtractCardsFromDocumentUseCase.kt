package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.DocumentCardCandidate
import java.util.regex.Pattern

/**
 * Heuristic Natural Language parser that extracts up to 35 flashcard candidates
 * from raw textbook notes, OCR dumps, and PDF text.
 */
class ExtractCardsFromDocumentUseCase {

    operator fun invoke(rawText: String, defaultTag: String = "Notes"): List<DocumentCardCandidate> {
        if (rawText.isBlank()) return emptyList()

        val normalizedText = rawText.replace("\r\n", "\n")
        val results = mutableListOf<DocumentCardCandidate>()
        val seenQuestions = mutableSetOf<String>()

        fun addCandidate(q: String, a: String, tag: String = defaultTag) {
            val cleanQ = q.trim().replace(Regex("^[-*•\\d.]+\\s*"), "")
            val cleanA = a.trim()
            if (cleanQ.length in 4..250 && cleanA.length in 8..500) {
                val normalizedKey = cleanQ.lowercase()
                if (!seenQuestions.contains(normalizedKey) && results.size < 35) {
                    seenQuestions.add(normalizedKey)
                    results.add(DocumentCardCandidate(cleanQ, cleanA, tag))
                }
            }
        }

        // Heuristic 1: Explicit Q&A patterns (e.g. Q: What is X? A: X is...)
        val qaPattern = Pattern.compile(
            "(?i)(?:^|\\n)(?:Q|Question)\\s*[:.]\\s*([^\\n?]{6,200}\\??)\\s*\\n+(?:A|Answer)\\s*[:.]\\s*([^\\n]{10,400})"
        )
        val qaMatcher = qaPattern.matcher(normalizedText)
        while (qaMatcher.find() && results.size < 35) {
            val q = qaMatcher.group(1)?.let { if (!it.endsWith("?")) "$it?" else it } ?: ""
            val a = qaMatcher.group(2) ?: ""
            addCandidate(q, a, "Q&A")
        }

        // Heuristic 2: Colon Definitions (e.g. Mitochondria: The powerhouse of the cell.)
        val colonPattern = Pattern.compile(
            "(?m)^(?!(?:Q|A|Question|Answer)\\b)([A-Z][A-Za-z0-9\\s\\-()]{2,50})\\s*:\\s+([A-Z][^.\\n]{15,350}\\.?)$"
        )
        val colonMatcher = colonPattern.matcher(normalizedText)
        while (colonMatcher.find() && results.size < 35) {
            val term = colonMatcher.group(1) ?: ""
            val definition = colonMatcher.group(2) ?: ""
            addCandidate("What is $term?", definition, "Definition")
        }

        // Heuristic 3: "is defined as" / "refers to"
        val definedAsPattern = Pattern.compile(
            "\\b([A-Z][a-zA-Z0-9\\s\\-]{2,45})\\s+(?:is defined as|refers to|means|is known as)\\s+([^.\\n]{15,350}[.!?])"
        )
        val definedAsMatcher = definedAsPattern.matcher(normalizedText)
        while (definedAsMatcher.find() && results.size < 35) {
            val term = definedAsMatcher.group(1) ?: ""
            val def = definedAsMatcher.group(2) ?: ""
            addCandidate("Define $term", def, "Concept")
        }

        // Heuristic 4: "is a / an ... that / which"
        val isAPattern = Pattern.compile(
            "\\b([A-Z][a-zA-Z0-9\\s\\-]{2,45})\\s+is\\s+(?:the|a|an)\\s+([a-z]{3,20}\\s+(?:that|which|by which|responsible for|used for|used to)\\s+[^.\\n]{15,350}[.!?])"
        )
        val isAMatcher = isAPattern.matcher(normalizedText)
        while (isAMatcher.find() && results.size < 35) {
            val term = isAMatcher.group(1) ?: ""
            val def = isAMatcher.group(2) ?: ""
            addCandidate("What is the role of $term?", "It is $def", "Concept")
        }

        // Heuristic 5: Purpose / Function patterns
        val funcPattern = Pattern.compile(
            "(?i)\\b(?:The\\s+)?(?:primary\\s+)?(?:function|purpose|role|goal)\\s+of\\s+(?:the\\s+)?([A-Za-z0-9\\s\\-]{3,45})\\s+is\\s+(?:to\\s+)?([^.\\n]{15,350}[.!?])"
        )
        val funcMatcher = funcPattern.matcher(normalizedText)
        while (funcMatcher.find() && results.size < 35) {
            val term = funcMatcher.group(1) ?: ""
            val def = funcMatcher.group(2) ?: ""
            addCandidate("What is the primary function of $term?", "To $def", "Function")
        }

        // Fallback: If no structured cards were matched, split by bullet points or paragraphs
        if (results.isEmpty()) {
            val lines = normalizedText.split("\n")
                .map { it.trim() }
                .filter { it.length in 25..350 }

            for ((idx, line) in lines.take(15).withIndex()) {
                val parts = line.split(" - ", " — ", " – ")
                if (parts.size >= 2) {
                    addCandidate(parts[0], parts[1], defaultTag)
                } else {
                    addCandidate("Key Point ${idx + 1}", line, defaultTag)
                }
            }
        }

        return results
    }
}
