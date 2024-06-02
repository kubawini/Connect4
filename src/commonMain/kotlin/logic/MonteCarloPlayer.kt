package logic

import korlibs.time.measureTimeWithResult
import logic.montecarlo.MonteCarloNode
import logic.montecarlo.SimpleMonteCarloAlgorithm

const val iterations: Int = 1_000_000

class MonteCarloPlayer : GamePlayer {
    // TODO: Convert to iterative solution where single game tree is extended through the game
    override fun chooseColumnToPlay(boardState: BoardState): Int {
        val node = MonteCarloNode(action = -1, boardState = boardState.copy(), parent = null)
        return measureTimeWithResult {
            SimpleMonteCarloAlgorithm().play(node, iterations)
        }.also {
            println("Time taken: ${it.time}")
        }.result
    }

}