package logic

import kotlin.jvm.JvmInline

internal const val Height: Int = 6
internal const val Width: Int = 7

data class BoardState(
    private var moves: Int = 0,
    private var position: ULong = 0uL,
    private var mask: ULong = 0uL,
    private var bottom: ULong = 0uL
) {
    val movesCount: Int
        get() = moves
    val key: ULong
        get() = position + mask + bottom

    fun canPlay(column: Int): Boolean {
        return (mask and topMask(column)) == 0uL
    }

    fun numberOfTokens(column: Int): Int {
        var m = (mask shr column * (Height + 1)) and ((1uL shl (Height + 1)) - 1uL)
        var result = 0
        while (m > 0uL) {
            m = m shr 1
            result += 1
        }
        return result
    }

    private fun topMask(column: Int): ULong {
        return (1uL shl (Height - 1)) shl column * (Height + 1)
    }

    fun play(column: Int): BoardState {
        position = position xor mask
        mask = mask or (mask + bottomMask(column))
        moves += 1
        return this
    }

    /**
     * Returns number between 1 and 50
     */
    fun getMoveKey(column: Int): MoveKey {
        val piece = (0b111_1111UL shl column * (Height + 1)) and (mask + bottomMask(column))
        if (piece and (piece - 1UL) != 0UL) {
            throw IllegalStateException("Illegal piece")
        }
        return MoveKey(piece.countTrailingZeroBits())
        .also {
            if (it.key > 49) throw IllegalStateException("Illegal play key $it")
        }
    }

    private fun bottomMask(column: Int): ULong {
        return 1uL shl column * (Height + 1)
    }

    private fun alignment(pos: ULong): Boolean {
        var m: ULong = pos and (pos shr (Height + 1))
        if (m and (m shr (2 * (Height + 1))) > 0uL) {
            return true
        }

        m = pos and (pos shr Height)
        if (m and (m shr (2 * Height)) > 0uL) {
            return true
        }

        m = pos and (pos shr (Height + 2))
        if (m and (m shr (2 * (Height + 2))) > 0uL) {
            return true
        }

        m = pos and (pos shr 1)
        if (m and (m shr 2) > 0uL) {
            return true
        }

        return false
    }

    fun isWinningMove(column: Int): Boolean {
        var futurePosition = position
        futurePosition = futurePosition or ((mask + bottomMask(column)) and columnMask(column))
        return alignment(futurePosition)
    }

    // TODO: verify whether it works
    fun isGameOver(): Boolean {
        return alignment(position xor mask) || isDraw()
    }

    fun isWonState(): Boolean {
        return alignment(position xor mask)
    }

    // TODO: Make sure this is correct
    fun isDraw(): Boolean {
        return mask and topMask(0) > 0uL
                && mask and topMask(1) > 0uL
                && mask and topMask(2) > 0uL
                && mask and topMask(3) > 0uL
                && mask and topMask(4) > 0uL
                && mask and topMask(5) > 0uL
    }

    private fun columnMask(column: Int): ULong {
        return ((1uL shl Height) - 1uL) shl column * (Height + 1)
    }

    fun restart() {
        moves = 0
        position = 0uL
        mask = 0uL
        bottom = 0uL
    }

}

@JvmInline
value class MoveKey(val key: Int) {
    val column: MoveColumn
        get() = MoveColumn(key / (Width + 1))
}

@JvmInline
value class MoveColumn(val column: Int)

