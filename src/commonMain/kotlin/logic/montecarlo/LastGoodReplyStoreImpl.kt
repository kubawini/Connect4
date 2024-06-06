package logic.montecarlo

import logic.MoveColumn
import logic.MoveKey

class LastGoodReplyStoreImpl : LastGoodReplyStore {

    // 2^12 since keys are 2*6=12 bit long
    private val replies: Array<IntArray> = arrayOf(IntArray(2 shl 12) { -1 }, IntArray(2 shl 12) { -1 })

    override fun store(move: MoveKey, reply: MoveColumn, player: Int) {
        replies[player][move.key] = reply.column
    }

    override fun store(prevMove: MoveKey, lastMove: MoveKey, reply: MoveColumn, player: Int) {
        val key = (prevMove.key shl 6) + lastMove.key
        replies[player][key] = reply.column
    }

    override fun getReply(move: MoveKey, player: Int): MoveColumn {
        return MoveColumn(replies[player][move.key])
    }

    override fun getReply(prevMove: MoveKey, lastMove: MoveKey, player: Int): MoveColumn {
        val key = (prevMove.key shl 6) + lastMove.key
        return MoveColumn(replies[player][key])
    }

    override fun reset() {
        replies[0].fill(-1)
        replies[1].fill(-1)
    }


}