package logic.montecarlo

import logic.BoardState
import logic.MoveKey
import logic.Width
import logic.montecarlo.MonteCarloGameResult.*
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class SimpleMonteCarloAlgorithm(
    private val c: Double = 1.414,
    private val useTranspositionTable: Boolean = true,
    private val lgr: Int = 2,
    private val printDebugInfo: Boolean = true
) : MonteCarloAlgorithm {
    private val random: Random = Random.Default
    private val actions: IntArray = (0 until 6).toList().toIntArray()
    private val transpositionTable = SimpleTranspositionTable()
    private val lastGoodReplyStore = LastGoodReplyStoreImpl()
    private var cachesHit = 0
    private var cachesMiss = 0
    private var repliesHit = 0
    private var repliesMiss = 0
    private var replies2Hit = 0
    private var replies2Miss = 0

    override fun play(root: MonteCarloNode, iterations: Int): Int {
        repeat(iterations) {
            play(root)
        }
        if (printDebugInfo) {
            println("Caches hit $cachesHit, caches miss $cachesMiss")
            println("Replies hit $repliesHit, replies miss $repliesMiss")
            println("Replies2 hit $replies2Hit, replies2 miss $replies2Miss")
        }
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
                val avgScore = if (useTranspositionTable) {
                    val key = it.boardState.key
                    val transpositionData = transpositionTable.get(key)
                    if (transpositionData != emptyTranspositionData) {
                        cachesHit++
                        transpositionData.avgScore
                    } else {
                        cachesMiss++
                        it.avgScore
                    }
                } else {
                    it.avgScore
                }
                ucb(q = avgScore, c = c, nParent = node.visits, nChild = it.visits)
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
        val moves: MutableList<MoveKey> = mutableListOf()
        var boardState = node.boardState.copy()
        while (true) {
            if (boardState.isGameOver()) {
                updateLastGoodReplies(moves, 1 - boardState.currentPlayer)
                return evaluateGame(boardState)
            }
            var action = -1
            if (lgr > 0 && moves.isNotEmpty()) {
                action = generateLastGoodReplyAction(moves = moves, boardState = boardState)
            }
            if (action < 0) {
                action = generateRandomAction(boardState)
            }
            moves.add(boardState.getMoveKey(action))
            boardState = boardState.play(action)
        }
    }

    private fun generateLastGoodReplyAction(
        moves: List<MoveKey>,
        boardState: BoardState
    ): Int {
        var column = -1
        if (lgr == 2 && moves.size > 1) {
            column = lastGoodReplyStore.getReply(
                prevMove = moves[moves.size - 1],
                lastMove = moves.last(),
                player = boardState.currentPlayer
            ).column
            if (column != -1) {
                replies2Hit++
            } else {
                replies2Miss++
            }
        }
        if (lgr > 0 && column == -1) {
            column = lastGoodReplyStore.getReply(moves.last(), boardState.currentPlayer).column
        }
        return if (column > 0 && boardState.canPlay(column)) {
            repliesHit++
            column
        } else {
            repliesMiss++
            -1
        }
    }

    private fun updateLastGoodReplies(moves: List<MoveKey>, winner: Int) {
        moves.asReversed()
            .windowed(size = if (lgr == 2) 3 else 2, step = 2)
            .forEach { lastMoves ->
                val recentMove = lastMoves[0]
                val prevMove = lastMoves[1]
                lastGoodReplyStore.store(move = prevMove, reply = recentMove.column, player = winner)
                if (lgr == 2) {
                    val prevPrevMove = lastMoves[2]
                    lastGoodReplyStore.store(
                        prevMove = prevPrevMove,
                        lastMove = prevMove,
                        reply = recentMove.column,
                        player = winner
                    )
                }
            }
    }

    private fun generateRandomAction(boardState: BoardState): Int {
        var action = -1
        while (action < 0) {
            val newAction = actions.random(random)
            if (boardState.canPlay(newAction)) {
                action = newAction
            }
        }
        return action
    }

    private fun backpropagation(start: MonteCarloNode, result: MonteCarloGameResult) {
        var node: MonteCarloNode? = start
        while (node != null) {
            val score = resultToScore(result, node.boardState.currentPlayer)
            node.updateScore(score)
            if (useTranspositionTable) {
                updateTranspositionTableData(score, node.boardState)
            }
            node = node.parent
        }
    }

    private fun updateTranspositionTableData(score: Int, boardState: BoardState) {
        val key = boardState.key
        val existingData = transpositionTable.get(key)
        val newData = existingData + transpositionData(1, score)
        transpositionTable.put(key, newData)
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
