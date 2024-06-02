package logic.montecarlo

interface MonteCarloAlgorithm {
    fun play(root: MonteCarloNode, iterations: Int): Int
}