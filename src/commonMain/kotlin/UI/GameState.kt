package UI

import Logic.BoardState

sealed class GameState {
    class InProgress() : GameState()
    class GameOver(val winner: Token) : GameState()
}

fun GameState.isGameOver(): Boolean {
    return this is GameState.GameOver
}