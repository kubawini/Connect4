package simulator

import logic.BoardState
import logic.GamePlayer

const val DRAW = -1
const val P1_WIN = 0
const val P2_WIN = 1

class Simulator(val debug: Boolean = false) {
    fun simulate(alg1Factory: () -> GamePlayer, alg2Factory: () -> GamePlayer, iterations: Int): SimulationResult {
        var alg1P1Wins = 0
        var alg1P2Wins = 0
        var alg2P1Wins = 0
        var alg2P2Wins = 0
        var draws = 0
        val alg1 = alg1Factory()
        val alg2 = alg2Factory()
        repeat(iterations) { i ->
            if (debug) {
                println("Iteration: $i")
            }
            when (simulate(alg1, alg2)) {
                P1_WIN -> alg1P1Wins++
                P2_WIN -> alg2P2Wins++
                DRAW -> draws++
            }
            alg1.reset()
            alg2.reset()
        }
        repeat(iterations) { i ->
            if (debug) {
                println("Iteration: $i")
            }
            when (simulate(alg2, alg1)) {
                P1_WIN -> alg2P1Wins++
                P2_WIN -> alg1P2Wins++
                DRAW -> draws++
            }
            alg1.reset()
            alg2.reset()
        }
        return SimulationResult(
            iterations = 2 * iterations,
            alg1Wins = Wins(asFirstPlayer = alg1P1Wins, asSecondPlayer = alg1P2Wins),
            alg2Wins = Wins(asFirstPlayer = alg2P1Wins, asSecondPlayer = alg2P2Wins),
            draws = draws
        )
    }

    private fun simulate(alg1: GamePlayer, alg2: GamePlayer): Int {
        val boardState = BoardState()
        var currentPlayer = 0
        val moves: MutableList<Int> = mutableListOf()
        while (!boardState.isGameOver()) {
            if (currentPlayer == 0) {
                val columnToPlay = alg1.chooseColumnToPlay(boardState)
                moves.add(columnToPlay)
                boardState.play(columnToPlay)
            } else {
                val columnToPlay = alg2.chooseColumnToPlay(boardState)
                moves.add(columnToPlay)
                boardState.play(columnToPlay)
            }
            currentPlayer = 1 - currentPlayer
        }
        return if (boardState.isWonState()) {
            1 - currentPlayer
        } else if (boardState.isDraw()) {
            DRAW
        } else {
            throw IllegalStateException("Simulator encountered illegal state")
        }
    }
}
