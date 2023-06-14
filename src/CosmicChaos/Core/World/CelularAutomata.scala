package CosmicChaos.Core.World

import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

import scala.util.Random

class CellularAutomata(val width: Int, val height: Int) {

  private val grassTextures: Array[TextureRegion] =
    TextureRegion.split(new Texture("data/images/world/tiles/grass.png"), 32, 32)(0)

  private val waterTextures: Array[TextureRegion] =
    TextureRegion.split(new Texture("data/images/world/tiles/water.png"), 32, 32)(0)

  var grid: Array[Array[Boolean]] = Array.ofDim[Boolean](width, height)
  val tileSize = 128 // Assuming a cell size of 50x50 pixels
  val numColumns = width / tileSize
  val numRows = height / tileSize
  val probabilityWall = 0.52f


  def worldCreation() = {
    //size world

    val random = new Random()

    // Initialize the grid randomly
    for (row <- 0 until numRows) {
      for (column <- 0 until numColumns) {
        grid(column)(row) = random.nextFloat() <= probabilityWall
      }
    }
    iterate(3)
  }

  def draw(g: GdxGraphics) = {
    g.clear(new Color(22/255.0f, 108/255.0f, 154/255.0f, 1))

    // Draw the map
    for (row <- 0 until numRows) {
      for (column <- 0 until numColumns) {
        val posX = column * tileSize
        val posY = row * tileSize

        val tileHash = ((posX + posY)*(posX + posY + 1)/2) + posY // https://stackoverflow.com/a/682617
        val tileTexture =
          if(grid(column)(row))
            grassTextures(tileHash % grassTextures.length)
          else
            waterTextures(tileHash % waterTextures.length)

        g.draw(tileTexture, posX + tileSize/2, posY - tileSize/2, tileSize, tileSize)
      }
    }
  }

  // Run a number of iterations to update the grid randomly
  def iterate(iterations: Int): Unit = {
    val newGrid = Array.ofDim[Boolean](numColumns, numRows)
    for (_ <- 0 until iterations) {
      for (x <- 0 until numColumns; y <- 0 until numRows) {
        val count = countAliveNeighbors(x, y) + (if(grid(x)(y)) 1 else 0)
        newGrid(x)(y) = shouldLive(grid(x)(y), count)
      }
      grid = newGrid
    }
  }

  // Count the number of alive neighbors around a cell
  def countAliveNeighbors(x: Int, y: Int): Int = {
    var count = 0
    for (dx <- -1 to 1;
         dy <- -1 to 1) {
      if (!(dx == 0 && dy == 0)) {
        val nx = x + dx
        val ny = y + dy
        if (nx >= 0 && nx < numColumns && ny >= 0 && ny < numRows && grid(nx)(ny)) {
          count += 1
        }
      }
    }

    count
  }

  // Determine whether a cell should be alive based on its current state and the count of alive neighbors
  private def shouldLive(isAlive: Boolean, count: Int): Boolean = {
    count > 4 || (isAlive && count > 3)
  }

  def getRandomClearPosition(clearingRadius: Int = 0): Vector2 = {
    def checkClear(px: Int, py: Int): Boolean = {
      for(x <- px - clearingRadius  to px + clearingRadius; y <- py - clearingRadius to py + clearingRadius) {
        if(x < 0 || x >= numColumns || y < 0 || y >= numRows || !grid(x)(y))
          return false
      }
      true
    }

    while(true) {
      val x = Random.between(clearingRadius + 1, numColumns - clearingRadius - 1)
      val y = Random.between(clearingRadius + 1, numRows - clearingRadius - 1)
      if(checkClear(x, y)) {
        return new Vector2(x * tileSize, y * tileSize)
      }
    }
    throw new Exception()
  }
}

