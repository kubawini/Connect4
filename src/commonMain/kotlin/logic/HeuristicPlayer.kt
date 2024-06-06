package logic

import kotlin.random.Random

private typealias Matrix = Array<IntArray>

class HeuristicPlayer : GamePlayer {
    private val random: Random = Random.Default
    override fun chooseColumnToPlay(boardState: BoardState): Int {
        val matrix: Matrix = boardState.toMatrix()
        val move = generateWinningMoveIfPossible(matrix, boardState)?.also {
            println("Generating winning move")
        }?.column ?: generateDefendingMoveIfNeeded(matrix, boardState)?.also {
            println("Generating defending move")
        }?.column ?: generateStrategicMoveIfPossible(matrix, boardState)?.also {
            println("Generating strategic move")
        }?.column ?: generateRandomAction(boardState).also {
            println("Generating random move")
        }.column
        return move
    }

    private fun generateWinningMoveIfPossible(matrix: Matrix, boardState: BoardState): MoveColumn? {
        return actions.firstOrNull { boardState.canPlay(it) && boardState.isWinningMove(it) }?.let { MoveColumn(it) }
    }

    private fun generateDefendingMoveIfNeeded(matrix: Matrix, boardState: BoardState): MoveColumn? {
        val opponentWinningMove = actions.firstOrNull { boardState.canPlay(it) && boardState.isOpponentWinningMove(it) }
        if (opponentWinningMove != null) {
            return MoveColumn(opponentWinningMove)
        }
        for (column in 1 until (Width - 1)) {
            val emptyCellRow = matrix.firstEmptySpace(column)
            if (emptyCellRow == -1) {
                continue
            }
            matrix[emptyCellRow][column] = 2
            val has3InARow =
                (matrix[emptyCellRow][0] == 0 && matrix[emptyCellRow][1] == 2 && matrix[emptyCellRow][2] == 2 && matrix[emptyCellRow][3] == 2 && matrix[emptyCellRow][4] == 0) ||
                        (matrix[emptyCellRow][1] == 0 && matrix[emptyCellRow][1] == 2 && matrix[emptyCellRow][2] == 2 && matrix[emptyCellRow][4] == 2 && matrix[emptyCellRow][5] == 0) ||
                        (matrix[emptyCellRow][2] == 0 && matrix[emptyCellRow][3] == 2 && matrix[emptyCellRow][4] == 2 && matrix[emptyCellRow][5] == 2 && matrix[emptyCellRow][6] == 0)
            matrix[emptyCellRow][column] = 0
            if (has3InARow) {
                return MoveColumn(column)
            }
        }
        return null
    }

    private fun generateStrategicMoveIfPossible(matrix: Matrix, boardState: BoardState): MoveColumn? {
        val centerEmptySpace = matrix.firstEmptySpace(3)
        if (centerEmptySpace in 0..3) {
            if (boardState.isMoveSafe(3)) {
                return MoveColumn(3)
            }
        }
        val player = boardState.movesCount % 2
        val availableStrategicActions = actions.filter { boardState.canPlay(it) }
            .filter { column -> matrix.firstEmptySpace(column) % 2 == player }
            .filter { column -> boardState.isMoveSafe(column) }
        return if (availableStrategicActions.isEmpty()) {
            null
        } else {
            MoveColumn(availableStrategicActions.random(random))
        }
    }

    private fun generateRandomAction(boardState: BoardState): MoveColumn {
        val safeActions = actions.filter { boardState.canPlay(it) && boardState.isMoveSafe(it) }
        return if (safeActions.isEmpty()) {
            MoveColumn(boardState.generateRandomAction(random))
        } else {
            MoveColumn(boardState.generateRandomAction(random, safeActions.toIntArray()))
        }
    }
}

fun BoardState.isMoveSafe(column: Int): Boolean {
    val boardState = this.copy()
    boardState.play(column)
    return actions.none { boardState.canPlay(it) && boardState.isWinningMove(it) }
}

/**
 * Returns row of first empty space in matrix in given column
 *
 * -1 if column is full
 */
private fun Matrix.firstEmptySpace(column: Int): Int {
    for (i in 0 until Height) {
        if (this[i][column] == 0) {
            return i
        }
    }
    return -1
}
