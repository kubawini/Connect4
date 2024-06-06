package logic

import korlibs.time.measureTimeWithResult
import logic.montecarlo.MonteCarloAlgorithm
import logic.montecarlo.MonteCarloNode
import logic.montecarlo.SimpleMonteCarloAlgorithm

const val iterations: Int = 100_000

class MonteCarloPlayer : GamePlayer {
    private val algorithm: MonteCarloAlgorithm = SimpleMonteCarloAlgorithm()
    override fun chooseColumnToPlay(boardState: BoardState): Int {
        return measureTimeWithResult {
            algorithm.play(boardState, iterations)
        }.also {
            println("Time taken: ${it.time}")
        }.result
    }

}