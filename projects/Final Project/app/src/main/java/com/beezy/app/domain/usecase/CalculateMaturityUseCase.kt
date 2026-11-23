package com.beezy.app.domain.usecase

import com.beezy.app.domain.model.Card
import kotlin.math.roundToInt

class CalculateMaturityUseCase {

    operator fun invoke(cards: List<Card>): Int {
        if (cards.isEmpty()) return 0
        val matureCount = cards.count { it.isMature }
        return ((matureCount.toDouble() / cards.size) * 100).roundToInt()
    }
}
