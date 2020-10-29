package com.pilleo.conway

// http://rosettacode.org/wiki/Conway%27s_Game_of_Life#Kotlin

fun main() {
    val listOf = hashSetOf<Cell>(
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

data class Cell(val x: Int, val y: Int, var isAlive:Boolean = true) : Comparable<Cell> {
    val aliveNeighbours = mutableSetOf<Cell>()

        override fun compareTo(other: Cell): Int {
        return if (this.x == other.x) {
            this.y - other.y
        } else {
            this.x - other.x
        }
    }
}

class ConwayOOP(private val height: Int,
                private val width: Int,
                private val endOfTheWorldPortals: Boolean,
                private val board : Array<Array<Cell>> = Array(height) { row ->
                    Array(width) { column -> Cell(column, row, false) }
                }
) {

    tailrec fun play(currentWorld: Set<Cell>, prevWorlds: Set<Set<Cell>> = HashSet()): Set<Set<Cell>> {
        if (currentWorld.isEmpty() || prevWorlds.contains(currentWorld)) return prevWorlds

        printBoard(currentWorld)
        val newWorldFromCurrentWorld: Set<Cell> = getNewWorldFromOldWorld(currentWorld)
        return play(newWorldFromCurrentWorld, prevWorlds.plusElement(currentWorld))
    }

    private fun getNewWorldFromOldWorld(oldMap: Set<Cell>): Set<Cell> {

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

            return of
                .asSequence()
                //.map{teleportOutOfWorldCellToOtherEnd(it)}
                      .filter { it.x >= 0 }
                      .filter { it.x < width }
                      .filter { it.y < height }
                      .filter { it.y >= 0 }
                .toHashSet()
        }

        fun getAliveNeighbours(inputCell:Cell): Set<Cell> {
          //  return if(inputCell.aliveNeighbours.isEmpty()){
                val neighbours: Set<Cell> = getAllPossibleNeighbours(inputCell)
                val toHashSet = neighbours.filter { cell -> oldMap.contains(cell) }.toHashSet()
                inputCell.aliveNeighbours.addAll(toHashSet)
             return   toHashSet

        }

        fun deadCellWillBeAlive(cell:Cell): Boolean = getAliveNeighbours(cell).size == 3

        fun aliveCellWillLive(cell:Cell): Boolean {
            val size: Int = getAliveNeighbours(cell).size
            return size == 2 || size == 3
        }

        fun cellIsAlive(cell: Cell): Boolean = oldMap.contains(cell)

        fun cellWillLive(e: Cell): Boolean {
            return if (cellIsAlive(e)) {
                val aliveCellWillLive = aliveCellWillLive(e)

                if(!aliveCellWillLive){
                    e.aliveNeighbours.forEach { it.aliveNeighbours.remove(e) }
                }

                aliveCellWillLive
            } else {
                val deadCellWillBeAlive = deadCellWillBeAlive(e)
                deadCellWillBeAlive
            }
        }

        return oldMap
            .flatMap { getAllPossibleNeighbours(it).plusElement(it) }
            .filter { cellWillLive(it) }
            .toSortedSet()
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