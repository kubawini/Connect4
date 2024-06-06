package logic.montecarlo

import logic.BoardState

interface MonteCarloAlgorithm {
    fun play(boardState: BoardState, iterations: Int): Int
    fun reset()
}