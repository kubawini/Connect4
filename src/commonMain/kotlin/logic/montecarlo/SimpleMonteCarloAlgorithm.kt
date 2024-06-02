package logic.montecarlo

import logic.BoardState
import logic.Width
import logic.montecarlo.MonteCarloGameResult.*
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class SimpleMonteCarloAlgorithm(private val c: Double = 1.414) : MonteCarloAlgorithm {
    val random: Random = Random.Default

    override fun play(root: MonteCarloNode, iterations: Int): Int {
        repeat(iterations) {
            play(root)
        }
        println(root.children)
        return root.children.maxByOrNull { it.avgScore }?.action ?: -1
    }

    private fun play(root: MonteCarloNode) {
        var simulatedPlayer = root.boardState.currentPlayer
        var leafNode = selection(root)
        if (leafNode.visits > 0 && !leafNode.boardState.isGameOver()) {
            leafNode = expansion(leafNode)
        }
        val simulationResult = simulation(leafNode)
        backpropagation(leafNode, simulationResult)
    }

    private fun selection(root: MonteCarloNode): MonteCarloNode {
        var node = root
        while (true) {
            val selectedNode: MonteCarloNode? = node.children.maxByOrNull {
                ucb(q = it.avgScore, c = c, nParent = node.visits, nChild = it.visits)
            }
            if (selectedNode == null) {
                return node
            }
            node = selectedNode
        }
    }

    private fun expansion(node: MonteCarloNode): MonteCarloNode {
        if (node.children.isNotEmpty()) {
            throw IllegalStateException("Error: Extending node with children not allowed")
        }
        val expandedNode = (0 until Width)
            .filter { action -> node.boardState.canPlay(action) }
            .map { action ->
                val nextState = node.boardState.copy().play(action)
                MonteCarloNode(action = action, boardState = nextState, parent = node)
            }.also {
                node.addChildren(it)
            }.firstOrNull()
        if (expandedNode == null) {
            throw IllegalStateException("Error: Unexpected state node can't be expended")
        }
        return expandedNode
    }

    private fun simulation(node: MonteCarloNode): MonteCarloGameResult {
        var boardState = node.boardState.copy()
        while (true) {
            if (boardState.isGameOver()) {
                return evaluateGame(boardState)
            }
            val availableActions = (0 until Width).filter { action -> boardState.canPlay(action) }
            val action = availableActions.random(random)
            boardState = boardState.play(action)
        }
    }

    private fun backpropagation(start: MonteCarloNode, result: MonteCarloGameResult) {
        var node: MonteCarloNode? = start
        while (node != null) {
            val score = resultToScore(result, node.boardState.currentPlayer)
            node.updateScore(score)
            node = node.parent
        }
    }

    private fun evaluateGame(boardState: BoardState): MonteCarloGameResult {
        if (boardState.isWonState()) {
            if (boardState.currentPlayer == 1) {
                return P0_WIN
            } else {
                return P1_WIN
            }
        } else if (boardState.isDraw()) {
            return DRAW
        }
        throw IllegalStateException("Illegal board state as terminal state")
    }

    private fun resultToScore(result: MonteCarloGameResult, currentPlayer: Int): Int {
        return when {
            result == DRAW -> 1
            result == P0_WIN && currentPlayer == 1 -> 2
            result == P1_WIN && currentPlayer == 0 -> 2
            else -> 0
        }
    }

    private fun ucb(q: Double, c: Double, nParent: Int, nChild: Int): Double {
        if (nChild == 0) {
            return Double.MAX_VALUE
        }
        return q + c * sqrt(ln(nParent.toDouble()) / nChild)
    }
}

val BoardState.currentPlayer: Int
    get() = movesCount % 2
