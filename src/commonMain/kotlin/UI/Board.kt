package UI

import Logic.*
import board
import cellSize
import fieldHeight
import fieldLeftIndent
import fieldTopIndent
import fieldWidth
import gameOver
import korlibs.image.color.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.math.geom.Circle
import leftIndent
import radius
import topIndent

fun Container.board(stage: Stage, monteCarloPlayer: MonteCarloPlayer = DummyPlayer()): Board {
    return Board(stage, monteCarloPlayer).addTo(this)
}

class Board(
    override val stage: Stage?,
    private val monteCarloPlayer: MonteCarloPlayer
) : Container() {
    var columns = emptyArray<Column>()

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

        if (stage != null) {
            for (i in 0..6) {
                val column = column(i, stage!!)
                column.addTo(this)
                columns += column
            }
        }
    }

    fun restart() {
        for (col in columns) {
            col.restart()
        }
        board.restart()
        gameOver = false
    }

    fun playAI() {
        val columnIndex = monteCarloPlayer.chooseColumnToPlay(board)
        if (board.isWinningMove(columnIndex)) {
            gameOver = true
        }
        var column = columns.get(columnIndex)
        var tokensInColumn = board.numberOfTokens(columnIndex)
        board.play(columnIndex)
        column.throwTokenbyAI(tokensInColumn)
    }

}
