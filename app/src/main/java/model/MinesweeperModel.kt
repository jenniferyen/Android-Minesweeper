package model

import java.util.*

object MinesweeperModel {

    // custom Coordinate class, x = numNearbyMines, y = state
    data class Coordinate(var x: Int, var y: Int)

    // board states
    private const val MINE = -1
    private const val COVERED = 0
    const val UNCOVERED = 1
    const val FLAGGED = 2

    // modes
    const val NOTFLAGGING: Int = -3
    const val FLAGGING: Int = 3
    var mode = NOTFLAGGING

    var numMines = 3
    var numFlags = 3

    var gameOver = false
    var gameWon = false

    // default 5x5 as per instructions
    var numRows = 5
    var numColumns = 5

    private var board = generateBoard()

    // generate random board
    private fun generateBoard(): Array<Array<Coordinate>> {
        var newBoard = Array(numColumns) {
            Array(numRows) {
                // initialized with no mines nearby and state COVERED
                Coordinate(0, COVERED)
            }
        }
        placeMines(newBoard)
        for (i in 0 until numColumns) {
            for (j in 0 until numRows) {
                if (newBoard[i][j].x != MINE) {
                    newBoard[i][j].x = countNearbyMines(newBoard, i, j)
                }
            }
        }
        return newBoard
    }

    private fun placeMines(board: Array<Array<Coordinate>>) {
        val random = Random()
        for (i in 1..numMines) {
            var randomX = random.nextInt(numColumns)
            var randomY = random.nextInt(numRows)
            // println("placing mine at $randomX, $randomY")

            // generate new location if there is already a mine at (randomX, randomY)
            while (board[randomX][randomY].x == MINE) {
                randomX = random.nextInt(numColumns)
                randomY = random.nextInt(numRows)
            }
            board[randomX][randomY].x = MINE
        }
    }

    fun checkMine(x: Int, y: Int): Boolean {
        return board[x][y].x == MINE
    }

    fun getState(x: Int, y: Int): Int {
        return board[x][y].y
    }

    fun getNumMines(x: Int, y: Int): Int {
        return board[x][y].x
    }

    // depends on mode: NOTFLAGGING or FLAGGING
    fun updateState(tX: Int, tY: Int) {
        if (mode == NOTFLAGGING) {
            updateStateHelper(tX, tY, UNCOVERED)
            if (board[tX][tY].x == 0) {
                uncoverSurroundingSquares(tX, tY)
            }
        }
        // either flag or unflag cell
        if (mode == FLAGGING) {
            val currState = getState(tX, tY)
            if (currState == FLAGGED) {
                updateStateHelper(tX, tY, COVERED)
            } else if (currState == COVERED) {
                if (numFlags != 0) {
                    updateStateHelper(tX, tY, FLAGGED)
                }
            }
        }
    }

    private fun uncoverSurroundingSquares(tX: Int, tY: Int) {
        if (tX - 1 >= 0 && tY - 1 >= 0 && (board[tX - 1][tY - 1].y) != UNCOVERED) {
            updateState(tX - 1, tY - 1)
        }
        if (tY - 1 >= 0 && (board[tX][tY - 1].y) != UNCOVERED) {
            updateState(tX, tY - 1)
        }
        if (tX + 1 < numColumns && tY - 1 >= 0 && (board[tX + 1][tY - 1].y) != UNCOVERED) {
            updateState(tX + 1, tY - 1)
        }
        if (tX - 1 >= 0 && (board[tX - 1][tY].y) != UNCOVERED) {
            updateState(tX - 1, tY)
        }
        if (tX + 1 < numColumns && (board[tX + 1][tY].y) != UNCOVERED) {
            updateState(tX + 1, tY)
        }
        if (tX - 1 >= 0 && tY + 1 < numRows && (board[tX - 1][tY + 1].y) != UNCOVERED) {
            updateState(tX - 1, tY + 1)
        }
        if (tY + 1 < numRows && (board[tX][tY + 1].y) != UNCOVERED) {
            updateState(tX, tY + 1)
        }
        if (tX + 1 < numColumns && tY + 1 < numRows && (board[tX + 1][tY + 1].y) != UNCOVERED) {
            updateState(tX + 1, tY + 1)
        }
    }

    private fun countNearbyMines(array: Array<Array<Coordinate>>, i: Int, j: Int): Int {
        var numMines = 0

        if (i - 1 >= 0 && j - 1 >= 0 && array[i - 1][j - 1].x == MINE) {
            numMines += 1
        }
        if (i - 1 >= 0 && array[i - 1][j].x == MINE) {
            numMines += 1
        }
        if (i - 1 >= 0 && j + 1 < numRows && array[i - 1][j + 1].x == MINE) {
            numMines += 1
        }
        if (j - 1 >= 0 && array[i][j - 1].x == MINE) {
            numMines += 1
        }
        if (i + 1 < numColumns && j - 1 >= 0 && array[i + 1][j - 1].x == MINE) {
            numMines += 1
        }
        if (i + 1 < numColumns && array[i + 1][j].x == MINE) {
            numMines += 1
        }
        if (i + 1 < numColumns && j + 1 < numRows && array[i + 1][j + 1].x == MINE) {
            numMines += 1
        }
        if (j + 1 < numRows && array[i][j + 1].x == MINE) {
            numMines += 1
        }
        return numMines
    }

    private fun updateStateHelper(x: Int, y: Int, state: Int) {
        board[x][y].y = state

        if (state == FLAGGED) {
            numFlags--
        } else if (state == COVERED) {
            numFlags++
        }

        if (checkGameOver()) {
            println("gameOver")
            uncoverMines()
        }
        if (checkGameWon()) {
            println("gameWon")
        }
    }

    private fun checkGameOver(): Boolean {
        for (i in 0 until numColumns) {
            for (j in 0 until numRows) {
                if (board[i][j].x == MINE && board[i][j].y == UNCOVERED) {
                    gameOver = true
                }
            }
        }
        return gameOver
    }

    private fun checkGameWon(): Boolean {
        var temp = 0
        for (i in 0 until numColumns) {
            for (j in 0 until numRows) {
                if (board[i][j].x != MINE && board[i][j].y == UNCOVERED && !gameOver) {
                    temp++
                }
            }
        }
        gameWon = (temp + numMines == (numRows * numColumns))
        return gameWon
    }

    private fun uncoverMines() {
        for (i in 0 until numColumns) {
            for (j in 0 until numRows) {
                if (board[i][j].x == MINE) {
                    board[i][j].y = UNCOVERED
                }
            }
        }
    }

    // restore default values
    fun resetModel() {
        gameOver = false
        gameWon = false
        mode = NOTFLAGGING
        numFlags = 3
        board = generateBoard()
    }

}