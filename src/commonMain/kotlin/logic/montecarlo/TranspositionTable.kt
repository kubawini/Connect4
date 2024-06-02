package logic.montecarlo

import kotlin.jvm.JvmInline

interface TranspositionTable {
    fun put(key: ULong, data: TranspositionData)
    fun get(key: ULong): TranspositionData
}

@JvmInline
value class TranspositionData(val data: Long) {
    operator fun plus(transpositionData: TranspositionData): TranspositionData {
        return TranspositionData(this.data + transpositionData.data)
    }
}

val TranspositionData.visits: Int
    get() = (data shr 32).toInt()

val TranspositionData.scoresSum: Int
    get() = data.toInt()

val TranspositionData.avgScore: Double
    get() = scoresSum / visits.toDouble()

internal fun transpositionData(visits: Int, scoresSum: Int): TranspositionData {
    return TranspositionData(scoresSum.toLong() + visits.toLong().shl(32))
}

val emptyTranspositionData: TranspositionData = transpositionData(0, 0)

