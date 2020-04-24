package ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.minesweeper.MainActivity
import com.example.minesweeper.R
import model.MinesweeperModel
import model.MinesweeperModel.FLAGGED
import model.MinesweeperModel.FLAGGING
import model.MinesweeperModel.UNCOVERED


class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val coveredBoard = Paint()
    private val uncoveredBoard = Paint()

    private val paintLine = Paint()

    private val colorMine = Paint()
    private val colorFlag = Paint()
    private val colorNum = Paint()

    init {
        coveredBoard.color = Color.BLACK
        uncoveredBoard.color = Color.GRAY

        paintLine.color = Color.WHITE
        paintLine.strokeWidth = 10F
        paintLine.style = Paint.Style.STROKE

        colorMine.color = Color.RED
        colorMine.textSize = 100F

        colorFlag.color = Color.GREEN
        colorFlag.textSize = 100F

        colorNum.color = Color.BLUE
        colorNum.textSize = 100F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), coveredBoard)
        drawBoard(canvas)
        drawSymbols(canvas)
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)
    }

    private fun drawBoard(canvas: Canvas?) {
        // horizontal lines
        for (i in 0 until MinesweeperModel.numRows) {
            canvas?.drawLine(
                0f, (i * height / MinesweeperModel.numRows).toFloat(),
                width.toFloat(),
                (i * height / MinesweeperModel.numRows).toFloat(),
                paintLine
            )
        }
        // vertical lines
        for (i in 0 until MinesweeperModel.numColumns) {
            canvas?.drawLine(
                (i * width / MinesweeperModel.numColumns).toFloat(),
                0f, (i * width / MinesweeperModel.numColumns).toFloat(),
                height.toFloat(),
                paintLine
            )
        }
    }

    private fun drawSymbols(canvas: Canvas?) {
        for (i in 0 until MinesweeperModel.numColumns) {
            for (j in 0 until MinesweeperModel.numRows) {
                drawSymbolsHelper(i, j, canvas)
            }
        }
    }

    private fun drawSymbolsHelper(x: Int, y: Int, canvas: Canvas?) {
        val currWidth = width.toFloat() / MinesweeperModel.numColumns
        val currHeight = height.toFloat() / MinesweeperModel.numRows

        val currState = MinesweeperModel.getState(x, y)

        if (currState == UNCOVERED) {
            // uncover pos then display either mine or numNearbyMines
            canvas?.drawRect(
                x * currWidth,
                y * currHeight,
                (x + 1) * currWidth,
                (y + 1) * currHeight,
                uncoveredBoard
            )
            if (MinesweeperModel.checkMine(x, y)) {
                canvas?.drawText(
                    "X",
                    x * currWidth + (.15 * currWidth).toFloat(),
                    (y + 1) * currHeight - (.15 * currHeight).toFloat(),
                    colorMine
                )
                // println("drawing mine at $x, $y")
            } else {
                var numMines = MinesweeperModel.getNumMines(x, y)
                if (numMines != 0) {
                    canvas?.drawText(
                        numMines.toString(),
                        x * currWidth + (.15 * currWidth).toFloat(),
                        (y + 1) * currHeight - (.15 * currHeight).toFloat(),
                        colorNum
                    )
                    // println("drawing numMines at $x, $y")
                }
            }

        }
        if (currState == FLAGGED) {
            // println("drawing flag at $x, $y")
            canvas?.drawText(
                "⚐",
                x * currWidth + (.15 * currWidth).toFloat(),
                (y + 1) * currHeight - (.15 * currHeight).toFloat(),
                colorFlag
            )
        }
    }

    private fun inRange(tX: Int, tY: Int): Boolean {
        return (tX < MinesweeperModel.numColumns) && (tY < MinesweeperModel.numRows)
                && (tX >= 0) && (tY >= 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val tX = event.x.toInt() / (width / MinesweeperModel.numColumns)
        val tY = event.y.toInt() / (height / MinesweeperModel.numRows)

        if (!MinesweeperModel.gameOver && !MinesweeperModel.gameWon && inRange(tX, tY)) {
            // check flag count, update state
            if (MinesweeperModel.mode == FLAGGING && MinesweeperModel.numFlags == 0) {
                (context as MainActivity).showSnackbar(context.getString(R.string.no_flags))
            }
            MinesweeperModel.updateState(tX, tY)
            (context as MainActivity).showNumFlags(MinesweeperModel.numFlags)
        }
        if (MinesweeperModel.gameOver) {
            (context as MainActivity).showSnackbar(context.getString(R.string.game_over))
        } else if (MinesweeperModel.gameWon) {
            (context as MainActivity).showSnackbar(context.getString(R.string.game_won))
        }

        invalidate()
        return super.onTouchEvent(event)
    }

    fun restart() {
        MinesweeperModel.resetModel()
        invalidate()
    }

}