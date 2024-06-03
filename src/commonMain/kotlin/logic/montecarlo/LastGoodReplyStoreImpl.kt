package logic.montecarlo

import logic.MoveColumn
import logic.MoveKey

class LastGoodReplyStoreImpl : LastGoodReplyStore {

    // 2^12 since keys are 2*6=12 bit long
    private val replies: Array<IntArray> = arrayOf(IntArray(2 shl 12) { -1 }, IntArray(2 shl 12) { -1 })

    override fun store(move: MoveKey, reply: MoveColumn, player: Int) {
        replies[player][move.key] = reply.column
    }

    override fun store(move1: MoveKey, move2: MoveKey, reply: MoveColumn, player: Int) {
        TODO("Not yet implemented")
    }

    override fun getReply(move: MoveKey, player: Int): MoveColumn {
        return MoveColumn(replies[player][move.key])
    }

    override fun getReply(move1: MoveKey, move2: MoveKey, player: Int): MoveColumn {
        TODO("Not yet implemented")
    }


}