package Logic

val Height: Int = 6

open class BoardState() {

    private var moves: Int = 0
    var position: ULong = 0uL
        private set
    private var mask: ULong = 0uL
    private var bottom: ULong = 0uL

    private var key: ULong = 0uL
        get() = position + mask + bottom

    fun canPlay(column: Int): Boolean {
        return (mask and topMask(column)) == 0uL
    }

    fun numberOfTokens(column: Int): Int{
        var m = (mask shr column * (Height + 1)) and ((1uL shl (Height + 1)) - 1uL)
        var result = 0
        while(m > 0uL){
            m = m shr 1
            result += 1
        }
        return result
    }

    private fun topMask(column: Int): ULong{
        return (1uL shl (Height - 1)) shl column * (Height + 1)
    }

    fun play(column: Int){
        position = position xor mask
        mask = mask or (mask + bottom_mask(column))
        moves += 1
    }

    private fun bottom_mask(column: Int): ULong{
        return 1uL shl column * (Height + 1)
    }

    fun alignment(pos: ULong): Boolean{
        var m: ULong = pos and (pos shr (Height + 1))
        if(m and (m shr (2 * (Height + 1))) > 0uL){
            return true
        }

        m = pos and (pos shr Height)
        if(m and (m shr (2 * Height)) > 0uL){
            return true
        }

        m = pos and (pos shr (Height + 2))
        if(m and (m shr (2 * (Height + 2))) > 0uL){
            return true
        }

        m = pos and (pos shr 1)
        if(m and (m shr 2) > 0uL){
            return true
        }

        return false
    }

    fun isWinningMove(column: Int): Boolean{
        var futurePosition = position
        futurePosition = futurePosition or ((mask + bottom_mask(column)) and columnMask(column))
        return alignment(futurePosition)
    }

    fun columnMask(column: Int): ULong{
        return ((1uL shl Height) - 1uL) shl column * (Height + 1)
    }

    fun restart() {
        moves = 0
        position = 0uL
        mask = 0uL
        bottom = 0uL
    }

}
