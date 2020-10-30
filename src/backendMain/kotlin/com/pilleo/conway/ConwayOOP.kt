package com.pilleo.conway

import java.util.*
import kotlin.collections.HashSet

// http://rosettacode.org/wiki/Conway%27s_Game_of_Life#Kotlin

fun main() {
    val listOf = sortedSetOf<Cell>(
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
        if (this.y < other.y) return -1;
        if (this.y > other.y) return +1;
        if (this.x < other.x) return -1;
        if (this.x > other.x) return +1;
        return 0;
    }
}

class ConwayOOP(
        private val height: Int,
        private val width: Int,
        private val endOfTheWorldPortals: Boolean,
        private val board: Array<Array<Cell>> = Array(height) { row ->
            Array(width) { column -> Cell(column, row, false) }
        }
) {

    tailrec fun play(currentlyAliveCells: Set<Cell>, prevWorlds: Set<Set<Cell>> = HashSet()): Set<Set<Cell>> {
        val contains = prevWorlds.contains(currentlyAliveCells)
        if (currentlyAliveCells.isEmpty() || contains) return prevWorlds
        currentlyAliveCells.forEach { board[it.y][it.x].isAlive = true }

        printBoard(currentlyAliveCells)

        val (cellsToDie, cellsToLive) = findDeltaToChange(currentlyAliveCells)
                .map { it.copy() }
                .partition { it.isAlive }

        val sortedSetOf = TreeSet(currentlyAliveCells)

        cellsToLive.forEach { it.isAlive = true
            board[it.y][it.x].isAlive = true
        }

        cellsToDie.forEach {              board[it.y][it.x].isAlive = false}
        sortedSetOf.addAll(cellsToLive)
        sortedSetOf.removeAll(cellsToDie)
        val newWorldFromCurrentWorld: Set<Cell> = sortedSetOf

        return play(newWorldFromCurrentWorld, prevWorlds.plusElement(currentlyAliveCells))
    }

    private fun findDeltaToChange(oldMap: Set<Cell>): Set<Cell> {

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

        fun getAliveNeighbours(inputCell: Cell): Set<Cell> {
            return getNeighbours(inputCell).filterTo(HashSet()) { it.isAlive }
        }

        fun cellIsAlive(cell: Cell): Boolean = board[cell.y][cell.x].isAlive
        fun deadCellWillBecomeAlive(cell: Cell): Boolean = getAliveNeighbours(cell).size == 3

        fun aliveCellWillLive(cell: Cell): Boolean {
            val size: Int = getAliveNeighbours(cell).size
            return size == 2 || size == 3
        }

        fun cellWillChange(cell: Cell): Boolean {
            return if (cellIsAlive(cell)) {
                !aliveCellWillLive(cell)
            } else {
                deadCellWillBecomeAlive(cell)
            }
        }

        return oldMap
                .flatMap { getNeighbours(it).plusElement(it) }
                .filter { cellWillChange(it) }
                .distinct()
                .map { it.copy() }
                .toHashSet()

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