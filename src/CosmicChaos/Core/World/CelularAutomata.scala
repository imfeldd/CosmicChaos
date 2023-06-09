package CosmicChaos.Core.World

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color

import scala.util.Random

class CellularAutomata(val width: Int, val height: Int, seed: Long) {

  private val random = new Random(seed)
  private var grid: Array[Array[Boolean]] = Array.ofDim[Boolean](width, height)
  val tileSize = 128 // Assuming a cell size of 50x50 pixels
  val numColumns = width / tileSize
  val numRows = height / tileSize
  val probabilityWall = 0.3

  def worldCreation() = {
    //size world

    // Initialize the grid randomly
    for (row <- 0 until numRows) {

      for (column <- 0 until numColumns) {
        grid(column)(row) = random.nextFloat() < probabilityWall

      }
    }
    iterate(7)
  }

  def draw(g: GdxGraphics) = {
    for (row <- 0 until numRows) {

      for (column <- 0 until numColumns) {
        val posX = column * tileSize
        val posY = row * tileSize

        val cellColor = if (grid(column)(row)) Color.GRAY else Color.WHITE
        g.drawFilledRectangle(posX, posY, tileSize, tileSize, 0)
        g.setColor(cellColor)
      }
    }
  }



  // Run a number of iterations to update the grid randomly
  def iterate(iterations: Int): Unit = {
    for (_ <- 0 until iterations) {
      val newGrid = Array.ofDim[Boolean](width, height)

      for (x <- 0 until width; y <- 0 until height) {
        val count = countAliveNeighbors(x, y)
        newGrid(x)(y) = shouldLive(grid(x)(y), count)
      }
      grid = newGrid
    }
  }

  // Count the number of alive neighbors around a cell
  private def countAliveNeighbors(x: Int, y: Int): Int = {
    var count = 0
    for (dx <- -1 to 1; dy <- -1 to 1) {
      if (!(dx == 0 && dy == 0)) {
        val nx = (x + dx + width) % width
        val ny = (y + dy + height) % height
        if (grid(nx)(ny)) {
          count += 1
        }
      }
    }
    count
  }

  // Determine whether a cell should be alive based on its current state and the count of alive neighbors
  private def shouldLive(isAlive: Boolean, count: Int): Boolean = {
    if (isAlive) {
      count >= 2 && count <= 5
    } else {
      count >= 1 && count <= 6
    }
  }
}
