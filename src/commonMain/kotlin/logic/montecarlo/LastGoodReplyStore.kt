package logic.montecarlo

import logic.MoveColumn
import logic.MoveKey
import kotlin.jvm.JvmInline

interface LastGoodReplyStore {
    /**
     * Store a reply to a move for a player
     */
    fun store(move: MoveKey, reply: MoveColumn, player: Int)

    /**
     * Store a reply to 2 moves for a player.
     * @param move1 is move by player
     * @param move2 is move by oponent
     */
    fun store(move1: MoveKey, move2: MoveKey, reply: MoveColumn, player: Int)
    fun getReply(move: MoveKey, player: Int): MoveColumn
    fun getReply(move1: MoveKey, move2: MoveKey, player: Int): MoveColumn
}

