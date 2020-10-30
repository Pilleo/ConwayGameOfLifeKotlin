package com.pilleo.conway

// http://rosettacode.org/wiki/Conway%27s_Game_of_Life#Kotlin

fun main() {
    val listOf = linkedSetOf<Cell>(
        Cell(0, 1),
        Cell(1, 0),
        Cell(2, 2),
        Cell(3, 2),
        Cell(4, 2),
        Cell(5, 2),
        Cell(6, 2),
        Cell(7, 3),
        Cell(7, 4)
    )

    ConwayOOP(8, 8, true).play(listOf)
}

data class Cell(val x: Int, val y: Int, var isAlive: Boolean = true) : Comparable<Cell>, Cloneable {
    val aliveNeighbours = mutableSetOf<Cell>()

    override fun compareTo(other: Cell): Int {
        // val aliveVar = if (isAlive) 1 else -1

        return if (this.x == other.x) {
            this.y - other.y
        } else {
            this.x - other.x
        }
    }
}

data class Delta(val newAliveCells: List<Cell>,
                 val newDaedCells: List<Cell>)

class ConwayOOP(
    private val height: Int,
    private val width: Int,
    private val endOfTheWorldPortals: Boolean,
    private val board: Array<Array<Cell>> = Array(height) { row ->
        Array(width) { column -> Cell(column, row, false) }
    }
) {

    tailrec fun play(currentlyAliveCells: Set<Cell>, prevWorlds: Set<Set<Cell>> = HashSet()): Set<Set<Cell>> {
        if (currentlyAliveCells.isEmpty() || prevWorlds.contains(currentlyAliveCells)) return prevWorlds
        currentlyAliveCells.forEach { board[it.y][it.x].isAlive = true }

        printBoard(currentlyAliveCells)
        val (newAliveCells, newDaedCells) = getNewWorldFromOldWorld(currentlyAliveCells)
        newAliveCells.forEach { board[it.y][it.x].isAlive = true }
        newDaedCells.forEach { board[it.y][it.x].isAlive = false }

        val newWorldFromCurrentWorld: Set<Cell> = currentlyAliveCells
            .plus(newAliveCells)
            .minus(newDaedCells)


        return play(newWorldFromCurrentWorld, prevWorlds.plusElement(currentlyAliveCells))
    }

    private fun getNewWorldFromOldWorld(oldMap: Set<Cell>): Delta {

        fun getNeighbours(inputCell: Cell): Set<Cell> {

            val set: MutableSet<Cell> = HashSet()


            if (inputCell.x > 0) {
                set.add(board[inputCell.y][inputCell.x - 1])
                if (inputCell.y > 0) {
                    set.add(board[inputCell.y - 1][inputCell.x - 1])
                }
                if (inputCell.y < height - 1) {
                    set.add(board[inputCell.y + 1][inputCell.x - 1])
                }
            }

            if (inputCell.y > 0) {
                set.add(board[inputCell.y - 1][inputCell.x])

                if (inputCell.x < width - 1) {
                    set.add(board[inputCell.y - 1][inputCell.x + 1])
                }
            }

            if (inputCell.x < width - 1) {
                set.add(board[inputCell.y][inputCell.x + 1])

                if (inputCell.y < height - 1) {
                    set.add(board[inputCell.y + 1][inputCell.x + 1])
                }
            }

            if (inputCell.y < height - 1) {
                set.add(board[inputCell.y + 1][inputCell.x])
            }

            return set.mapTo(HashSet()) { it.copy() }
        }

        fun getAllPossibleNeighbours(cell: Cell): Set<Cell> {

            fun teleportOutOfWorldCellToOtherEnd(cell: Cell): Cell {

                val resX: Int =
                    when {
                        cell.x < 0 -> width - 1
                        cell.x >= width -> 0
                        else -> cell.x
                    }

                val resY: Int =
                    when {
                        cell.y >= height -> 0
                        cell.y < 0 -> height - 1
                        else -> cell.y
                    }

                return if (cell.x == resX && cell.y == resY) cell
                else Cell(resX, resY)
            }

            val x: Int = cell.x
            val y: Int = cell.y
            val of: MutableSet<Cell> = mutableSetOf(

                Cell(x, y - 1),
                Cell(x, y + 1),
                Cell(x + 1, y - 1),

                Cell(x + 1, y + 1)
            )

            if (x > 0) {
                of.add(Cell(x - 1, y))
                if (y > 0) {
                    of.add(Cell(x - 1, y - 1))
                }
                if (y < height - 1) {
                    of.add(Cell(x - 1, y + 1))
                }
            }

            if (x < width - 1) {
                of.add(Cell(x + 1, y))

            }

            val neighbours = getNeighbours(cell)
            val set = of
                .asSequence()
                //.map{teleportOutOfWorldCellToOtherEnd(it)}
                .filter { it.x >= 0 }
                .filter { it.x < width }
                .filter { it.y < height }
                .filter { it.y >= 0 }
                .toHashSet()
            return neighbours
        }



        fun getAliveNeighbours(inputCell: Cell): Set<Cell> {
            return getNeighbours(inputCell).filterTo(HashSet()) { it.isAlive }
        }

        fun cellIsAlive(cell: Cell): Boolean = board[cell.y][cell.x].isAlive
        fun deadCellWillBeAlive(cell: Cell): Boolean = getAliveNeighbours(cell).size == 3

        fun aliveCellWillLive(cell: Cell): Boolean {
            val size: Int = getAliveNeighbours(cell).size
            return size == 2 || size == 3
        }


        val (aliveCells, hotDeadCells) = oldMap
            .flatMap { getAllPossibleNeighbours(it).plusElement(it) }
            .partition { cellIsAlive(it) }

        val newlyKilledCells = aliveCells.filter { !aliveCellWillLive(it) }
        val newAliveCells = hotDeadCells.filter { deadCellWillBeAlive(it) }
newlyKilledCells.forEach { it.isAlive=false }
        newAliveCells.forEach { it.isAlive=true }
        return Delta(newAliveCells, newlyKilledCells)

    }

    private fun printBoard(currMapp: Set<Cell>) {
        val boardToPrintWorld: Array<BooleanArray> = Array(height) { (0 until width).map { false }.toBooleanArray() }

        currMapp.forEach { boardToPrintWorld[it.y][it.x] = true }

        for (currY: Int in boardToPrintWorld.indices) {
            for (element in boardToPrintWorld[currY]) {

                when {
                    element -> print(" 8")
                    else -> print(" .")
                }
            }
            println()
        }
        println()
        println()

    }

}