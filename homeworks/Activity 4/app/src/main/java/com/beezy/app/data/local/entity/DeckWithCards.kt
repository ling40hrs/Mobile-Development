package com.beezy.app.data.local.entity

data class DeckWithCards(
    val deck: DeckEntity,
    val cards: List<CardEntity>
) {
    val totalCards: Int get() = cards.size
    
    /**
     * Cards are considered mature once their SM-2 interval reaches 21 days or more.
     */
    val maturityPercentage: Int
        get() {
            if (cards.isEmpty()) return 0
            val matureCount = cards.count { it.interval >= 21 }
            return kotlin.math.round((matureCount.toDouble() / cards.size) * 100).toInt()
        }
}
