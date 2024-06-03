package ui

sealed class GameState {
    data object InProgress : GameState()
    class GameOver(val winner: Token) : GameState()
}

fun GameState.isGameOver(): Boolean {
    return this is GameState.GameOver
}