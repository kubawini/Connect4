package logic.montecarlo

import logic.BoardState

class MonteCarloNode(val action: Int, val boardState: BoardState, parent: MonteCarloNode?) {
    var visits: Int = 0
        private set
    var scoresSum: Int = 0
        private set
    var parent: MonteCarloNode? = parent
        private set
    private val _children: MutableList<MonteCarloNode> = mutableListOf()
    val children: List<MonteCarloNode> = _children

    val avgScore: Double
        get() = scoresSum / visits.toDouble()

    fun updateScore(score: Int) {
        visits++
        scoresSum += score
    }

    fun addChildren(nodes: List<MonteCarloNode>) {
        _children.addAll(nodes)
    }

    fun clearParent() {
        parent = null
    }
}