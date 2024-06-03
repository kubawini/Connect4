package ui

import cellSize
import columnHeight
import columnIndent
import fieldHeight
import fieldLeftIndent
import fieldTopIndent
import korlibs.image.color.RGBA
import korlibs.korge.animate.animator
import korlibs.korge.animate.moveBy
import korlibs.korge.input.onClick
import korlibs.korge.input.onOut
import korlibs.korge.view.*
import korlibs.time.milliseconds
import leftIndent
import radius


fun Container.column(number: Int, stage: Stage, onClick: suspend (column: Column) -> Unit) =
    Column(number, stage, onClick).addTo(this)

class Column(private val number: Int, override val stage: Stage, private val onClick: suspend (column: Column) -> Unit) :
    Container() {

    private var bgColumn = this.graphics {
        setColumnOpacity(0)
    }

    private var virtualToken = this.graphics {
        setVirtualTokenOpacity(0, Token.NONE)
    }


    init {
        position(leftIndent, columnIndent)
        onOut {
            this.hideHighlight()
            this.hideVirtualToken()
        }
        onClick {
            onClick(this)
        }
    }

    suspend fun throwToken(row: Int, token: Token) {
        val animator = animator(parallel = true)
        val tokenToBeThrown = createVirtualToken(255, token)
        animator.moveBy(
            tokenToBeThrown,
            y = fieldHeight - 1 - fieldTopIndent - row * cellSize,
            time = 1000.milliseconds
        )
        animator.awaitComplete()
    }

    fun showHighlight() {
        this.removeChild(bgColumn)
        setColumnOpacity(50)
    }

    fun showVirtualToken(token: Token) {
        this.setVirtualTokenOpacity(255, token)
    }

    fun hideVirtualToken() {
        this.removeChild(virtualToken)
    }

    private fun hideHighlight() {
        this.removeChild(bgColumn)
        setColumnOpacity(0)
    }

    private fun setColumnOpacity(a: Int) {
        bgColumn = this.graphics {
            fill(RGBA(155, 155, 155, a))
            {
                roundRect(
                    fieldLeftIndent + number * cellSize, 0f,
                    cellSize, columnHeight, 5f
                )
            }
        }
    }

    private fun setVirtualTokenOpacity(a: Int, token: Token) {
        virtualToken = createVirtualToken(a, token)
    }

    private fun createVirtualToken(opacity: Int, token: Token): Graphics {
        return this.graphics {
            fill(RGBA(token.rgb.r, token.rgb.g, token.rgb.b, opacity)) {
                circle(
                    korlibs.math.geom.Circle(
                        fieldLeftIndent + number * cellSize + cellSize / 2,
                        radius + fieldTopIndent,
                        radius
                    )
                )
            }
        }
    }

    fun restart() {
        this.removeChildren()
        hideHighlight()
        hideVirtualToken()
    }
}




