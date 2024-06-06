package logic.montecarlo

import logic.MoveColumn
import logic.MoveKey

interface LastGoodReplyStore {
    /**
     * Store a reply to a move for a player
     */
    fun store(move: MoveKey, reply: MoveColumn, player: Int)

    /**
     * Store a reply to sequence of 2 moves for a player.
     * @param prevMove is move by player
     * @param lastMove is move by oponent
     */
    fun store(prevMove: MoveKey, lastMove: MoveKey, reply: MoveColumn, player: Int)
    fun getReply(move: MoveKey, player: Int): MoveColumn
    fun getReply(prevMove: MoveKey, lastMove: MoveKey, player: Int): MoveColumn
    fun reset()
}

