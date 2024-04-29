package Logic

/* TODO Create classes implementing this interface
 chooseColumnToPlay should take current board (do not modify it)
 and return column to which AI should throw a token */
interface MonteCarloPlayer {
    fun chooseColumnToPlay(gameState: BoardState): Int
}
