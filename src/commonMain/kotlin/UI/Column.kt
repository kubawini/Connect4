package UI
import aiPlaying
import animationPending
import board
import boardVM
import cellSize
import columnHeight
import columnIndent
import currentToken
import fieldHeight
import fieldLeftIndent
import fieldTopIndent
import gameOver
import korlibs.image.color.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.time.*
import korlibs.korge.view.*
import korlibs.time.*
import leftIndent
import radius
import showPopup
import singlePlayerMode


fun Container.column(number: Int, stage: Stage) = Column(number, stage).addTo(this)

class Column(val number: Int, override val stage: Stage) : Container() {

    private var bgColumn = this.graphics {
        setColumnOpacity(0)
    }

    private var virtualToken = this.graphics {
        setVirtualTokenOpacity(0)
    }

    private var tokenToBeThrown = this.graphics {
        createVirtualToken(0)
    }

    init{
        position(leftIndent, columnIndent)
        onOver {
            if (!gameOver) {
                this.showHighlight()
                if(animationPending && !singlePlayerMode) {
                    this.showVirtualToken(nextToken())
                } else{
                    this.showVirtualToken()
                }
            }
        }
        onOut {
            if (!gameOver) {
                this.hideHighlight()
                this.hideVirtualToken()
            }
        }
        onClick {
            if (!gameOver and !animationPending) {
                val numberOfTokens = board.numberOfTokens(this.number)
                if (board.canPlay(number)) {
                    animationPending = true
                    aiPlaying = false
                    if (board.isWinningMove(number)) {
                        gameOver = true
                    }
                    board.play(this.number)
                    this.throwToken(numberOfTokens)
                    if(!singlePlayerMode){
                    }
                    else{
                        this.hideVirtualToken()
                        this.showVirtualToken()
                    }
                    if (gameOver) {
                        this.hideVirtualToken()
                        this.hideHighlight()
                    }
                    else if (!singlePlayerMode){
                        delay(1000.milliseconds)
                        aiPlaying = true
                        boardVM.playAI()
                        if(gameOver){
                            this.hideVirtualToken()
                            this.hideHighlight()
                        }
                    }
                }
            }
            stage.showPopup(boardVM)
        }
    }

    fun throwTokenbyAI(numberOfTokens: Int){
        throwToken(numberOfTokens)
    }

    private fun throwToken(numberOfTokens: Int){
        val animator = animator(parallel = true)
        showTokenToBeThrown()
        animator.parallel {
            moveBy(tokenToBeThrown,
                y = fieldHeight - 1 - fieldTopIndent - numberOfTokens * cellSize,
                time = 1000.milliseconds)
        }.block {
            if(singlePlayerMode or aiPlaying){
                animationPending = false
                aiPlaying = false
            }
        }
        switchToken()
    }

    private fun showHighlight(){
        this.removeChild(bgColumn)
        setColumnOpacity(50)
    }

    private fun showVirtualToken(token: Token = currentToken){
        this.setVirtualTokenOpacity(255, token)
    }

    private fun showTokenToBeThrown(token: Token = currentToken){
        this.tokenToBeThrown = createVirtualToken(255, token)
    }

    private fun hideVirtualToken(){
        this.removeChild(virtualToken)
    }

    private fun hideHighlight(){
        this.removeChild(bgColumn)
        setColumnOpacity(0)
    }

    private fun setColumnOpacity(a: Int){
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

    private fun setVirtualTokenOpacity(a: Int, token: Token = currentToken){
        virtualToken = createVirtualToken(a, token)
    }

    private fun createVirtualToken(opacity: Int, token: Token = currentToken): Graphics{
        return this.graphics {
            fill(RGBA(token.rgb.r, token.rgb.g, token.rgb.b, opacity)){
                circle(korlibs.math.geom.Circle(
                    fieldLeftIndent + number * cellSize + cellSize / 2,
                    radius + fieldTopIndent,
                    radius
                ))
            }
        }
    }

    fun restart(){
        this.removeChildren()
        hideHighlight()
        hideVirtualToken()
    }
}




