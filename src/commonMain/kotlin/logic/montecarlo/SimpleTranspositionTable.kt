package logic.montecarlo

import kotlin.math.sin

class SimpleTranspositionTable(private val size: Int = 8_000_000) : TranspositionTable {
    // 8_000_000 = 64 MB
    private val array: LongArray = LongArray(size)

    override fun put(key: ULong, data: TranspositionData) {
        array[(key % size.toUInt()).toInt()] = data.data
    }

    override fun get(key: ULong): TranspositionData {
        val data = array[(key % size.toUInt()).toInt()]
        return if (data == 0L) {
            emptyTranspositionData
        } else {
            TranspositionData(data)
        }
    }
}