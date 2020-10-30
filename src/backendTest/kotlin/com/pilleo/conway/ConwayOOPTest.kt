package com.pilleo.conway

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ConwayOOPTest {

    @Test
    internal fun testPlayBlinker() {

        val conwayOOP: ConwayOOP = ConwayOOP(3,3, true)

        val blinkerStartingSet:Set<Cell> = setOf(Cell(1,0), Cell(1,1), Cell(1,2))

        val playBlinker: LinkedHashSet<Set<Cell>> = conwayOOP.play(blinkerStartingSet, LinkedHashSet()) as LinkedHashSet<Set<Cell>>

        assertEquals(2,playBlinker.size)
        assertTrue { playBlinker.contains(blinkerStartingSet) }
        assertTrue { playBlinker.contains(setOf(Cell(0,1), Cell(1,1), Cell(2,1))) }
    }

    @Test
    internal fun playBeaconTest() {
        val conwayOOP: ConwayOOP = ConwayOOP(4,4, true)

        val beakonStartingSet:Set<Cell> = setOf(Cell(0,0), Cell(0,1), Cell(1,0), Cell(2,3), Cell(3,2), Cell(3,3))

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(beakonStartingSet)
        assertEquals(2,playBlinker.size)
        assertTrue { playBlinker.contains(beakonStartingSet) }
        assertTrue { playBlinker.contains(beakonStartingSet.plus(Cell(1,1)).plus( Cell(2,2))) }
    }

    @Test
    internal fun playToad(){
        val conwayOOP: ConwayOOP = ConwayOOP(6,6, true)

        val toadStartingSet:Set<Cell> =         arrayToCells(arrayOf(
            booleanArrayOf(false,false,false, false, false, false),
            booleanArrayOf(false,false,false, false, false, false),
            booleanArrayOf(false,false,true, true, true, false),
            booleanArrayOf(false,true,true, true, false, false),
            booleanArrayOf(false,false,false, false, false, false),
            booleanArrayOf(false,false,false, false, false, false)
        )
        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(2,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
        assertTrue { playBlinker.contains(arrayToCells(arrayOf(
            booleanArrayOf(false,false,false, false, false, false),
            booleanArrayOf(false,false,false, true, false, false),
            booleanArrayOf(false,true,false, false, true, false),
            booleanArrayOf(false,true,false, false, true, false),
            booleanArrayOf(false,false,true, false, false, false),
            booleanArrayOf(false,false,false, false, false, false)
        )
        )) }


    }

    @Test
    internal fun playBlock(){
        val conwayOOP: ConwayOOP = ConwayOOP(4,4, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1),
            intArrayOf(1,8,8,1),
            intArrayOf(1,8,8,1),
            intArrayOf(1,1,1,1)
        )
        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(1,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }


    @Test
    internal fun playBeehive(){
        val conwayOOP: ConwayOOP = ConwayOOP(5,6, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1,1,1),
            intArrayOf(1,1,8,8,1,1),
            intArrayOf(1,8,1,1,8,1),
            intArrayOf(1,1,8,8,1,1),
            intArrayOf(1,1,1,1,1,1)
        )
        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(1,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }

    @Test
    internal fun playLoaf(){
        val conwayOOP: ConwayOOP = ConwayOOP(6,6, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1,1,1),
            intArrayOf(1,1,8,8,1,1),
            intArrayOf(1,8,1,1,8,1),
            intArrayOf(1,1,8,1,8,1),
            intArrayOf(1,1,1,8,1,1),
            intArrayOf(1,1,1,1,1,1)
        )
        )
        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(1,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }


    @Test
    internal fun playBoat(){
        val conwayOOP: ConwayOOP = ConwayOOP(5,5, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1,1),
            intArrayOf(1,8,8,1,1),
            intArrayOf(1,8,1,8,1),
            intArrayOf(1,1,8,1,1),
            intArrayOf(1,1,1,1,1)
        )
        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(1,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }


    @Test
    internal fun playTubTest(){
        val conwayOOP: ConwayOOP = ConwayOOP(5,5, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1,1),
            intArrayOf(1,1,8,1,1),
            intArrayOf(1,8,1,8,1),
            intArrayOf(1,1,8,1,1),
            intArrayOf(1,1,1,1,1)
        )
        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(1,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }


    @Test
    internal fun playPentadecathlon(){
        val conwayOOP: ConwayOOP = ConwayOOP(11,18, true)

        val toadStartingSet:Set<Cell> =         intArrayToCells(arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,8,1,1,1,1,8,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,8,8,1,8,8,8,8,1,8,8,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,8,1,1,1,1,8,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)

            )

        )

        val playBlinker: Set<Set<Cell>> = conwayOOP.play(toadStartingSet)
        assertEquals(15,playBlinker.size)
        assertTrue { playBlinker.contains(toadStartingSet) }
    }


    internal fun arrayToCells(cells: Array<BooleanArray>) : Set<Cell>{
        val mapIndexed: List<List<Cell>> = cells.mapIndexed { index, booleans ->
            booleans.mapIndexed { i, b -> if (b) Cell(i, index) else Cell(-1, -1) }
                .filter { e -> e.y > 0 && e.x > 0 }


        }
        val set =TreeSet<Cell>()

        mapIndexed.forEach{e-> set.addAll(e)}
        return set
    }


    internal fun charsToCells(cells: Array<CharArray>) : Set<Cell>{
        val mapIndexed: List<List<Cell>> = cells.mapIndexed { index, booleans ->
            booleans.mapIndexed { i, b -> if (b=='W') Cell(i, index) else Cell(-1, -1) }
                .filter { e -> e.y > 0 && e.x > 0 }


        }
        val set =HashSet<Cell>()

        mapIndexed.forEach{e-> set.addAll(e)}
        return set
    }

    internal fun intArrayToCells(cells: Array<IntArray>) : Set<Cell>{
        val mapIndexed: List<List<Cell>> = cells.mapIndexed { index, booleans ->
            booleans.mapIndexed { i, b -> if (b==8) Cell(i, index) else Cell(-1, -1) }
                .filter { e -> e.y > 0 && e.x > 0 }


        }
        val set =HashSet<Cell>()

        mapIndexed.forEach{e-> set.addAll(e)}
        return set
    }
}
