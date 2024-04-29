package UI

import Logic.BoardState
import Logic.DummyPlayer
import Logic.MonteCarloPlayer
import cellSize
import fieldHeight
import fieldLeftIndent
import fieldTopIndent
import fieldWidth
import korlibs.image.color.Colors
import korlibs.io.async.launchImmediately
import korlibs.korge.input.onOver
import korlibs.korge.view.*
import korlibs.math.geom.Circle
import korlibs.math.geom.RectCorners
import korlibs.math.geom.Size
import kotlinx.coroutines.sync.Mutex
import leftIndent
import radius
import topIndent

fun Container.board(
    stage: Stage,
    monteCarloPlayer: MonteCarloPlayer = DummyPlayer(),
    onGameOver: (board: Board, GameState.GameOver) -> Unit
): Board {
    return Board(
        stage = stage,
        monteCarloPlayer = monteCarloPlayer,
        boardState = BoardState(),
        onGameOver = onGameOver
    ).addTo(this)
}

class Board(
    override val stage: Stage,
    private val monteCarloPlayer: MonteCarloPlayer,
    private val boardState: BoardState,
    private val onGameOver: (board: Board, GameState.GameOver) -> Unit
) : Container() {
    private var columns = emptyArray<Column>()
    private val lock: Mutex = Mutex()
    private var currentPlayer: Token = Token.YELLOW
    private var gameState: GameState = GameState.InProgress()

    init {
        roundRect(Size(fieldWidth, fieldHeight), RectCorners(5), fill = Colors["#0f1394"]) {
            position(leftIndent, topIndent)
        }

        this.graphics {
            fill(Token.NONE.rgb) {
                for (i in 0..6) {
                    for (j in 0..5) {
                        circle(
                            Circle(
                                fieldLeftIndent + i * cellSize + cellSize / 2,
                                fieldTopIndent + j * cellSize + cellSize / 2,
                                radius
                            )
                        )
                    }
                }
            }
        }.position(leftIndent, topIndent)

        for (columNumber in 0..6) {
            val column = column(columNumber, stage) { column ->
                if (currentPlayer != Token.YELLOW || gameState.isGameOver()) {
                    return@column
                }
                if (!lock.tryLock()) {
                    return@column
                }
                val rowNumber = boardState.numberOfTokens(columNumber)
                if (boardState.canPlay(columNumber)) {
                    playMove(columNumber, rowNumber, column)
                }
                lock.unlock()
            }
            columns += column
            column.addTo(this)
            column.onOver {
                if (!gameState.isGameOver()) {
                    column.showVirtualToken(Token.YELLOW)
                    column.showHighlight()
                }
            }
        }
    }

    private suspend fun playMove(columNumber: Int, rowNumber: Int, column: Column) {
        if (boardState.isWinningMove(columNumber)) {
            gameState = GameState.GameOver(currentPlayer)
        }
        boardState.play(columNumber)
        column.throwToken(rowNumber, currentPlayer)
        if (gameState.isGameOver()) {
            endTheGame()
        } else {
            switchPlayer()
            playAI()
            switchPlayer()
        }
    }

    fun restart() {
        for (col in columns) {
            col.restart()
        }
        boardState.restart()
        currentPlayer = Token.YELLOW
        gameState = GameState.InProgress()
    }

    suspend fun playAI() {
        val columnIndex = monteCarloPlayer.chooseColumnToPlay(boardState)
        if (boardState.isWinningMove(columnIndex)) {
            gameState = GameState.GameOver(currentPlayer)
        }
        val column = columns.get(columnIndex)
        val tokensInColumn = boardState.numberOfTokens(columnIndex)
        boardState.play(columnIndex)
        column.throwToken(tokensInColumn, currentPlayer)
        if (gameState.isGameOver()) {
            endTheGame()
        }
    }

    private fun switchPlayer() {
        if (currentPlayer == Token.YELLOW) {
            currentPlayer = Token.RED
        } else if (currentPlayer == Token.RED) {
            currentPlayer = Token.YELLOW
        }
    }

    private fun endTheGame() {
        if (gameState.isGameOver()) {
            onGameOver(this, gameState as GameState.GameOver)
        }
    }
}
