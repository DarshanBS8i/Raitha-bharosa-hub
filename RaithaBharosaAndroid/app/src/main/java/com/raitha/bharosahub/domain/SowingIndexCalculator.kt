package com.raitha.bharosahub.domain

import kotlin.math.max
import kotlin.math.min

object SowingIndexCalculator {
    fun calculate(
        moisture: Int,
        temp: Int,
        rainProb: Int,
        n: Int,
        p: Int,
        k: Int,
        plotSize: Int
    ): Int {
        // Simple weighted formula
        val moistureScore = when {
            moisture in 25..35 -> 100
            moisture in 20..40 -> 70
            else -> 30
        }
        val tempScore = when {
            temp in 22..28 -> 100
            temp in 18..32 -> 70
            else -> 40
        }
        val rainPenalty = if (rainProb > 60) 40 else 0
        
        val raw = (moistureScore * 0.6 + tempScore * 0.4).toInt() - rainPenalty
        return max(0, min(100, raw))
    }
}
