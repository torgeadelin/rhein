package examples.gameOfLife

import rhein.Behaviour
import rhein._

// class Cell(
//     var id: String = java.util.UUID.randomUUID().toString(),
//     var alive: Boolean = false,
//     var neighbours: List[Behaviour[Cell]] = List()
// ) {
//   def makeAlive() = {
//     if (alive) throw new Error(s"${id} is already alive")
//     alive = true
//     this
//   }

//   def addNeighbour(neighbour: Behaviour[Cell]) = {
//     neighbour :: neighbours
//     this
//   }
// }

class GameOfLife {
  type World = Array[Boolean]

  val WIDTH = 3
  val HEIGHT = 3

  val tickStream: EventSink[Unit] = new EventSink()

  val t = new java.util.Timer()
  val task = new java.util.TimerTask {
    def run() = tickStream.send(Unit)
  }
  t.schedule(task, 2000L, 2000L)

  var eventLoop: EventLoop[World] = new EventLoop()
  var cState = eventLoop.hold(createWorld())
  eventLoop.loop(tickStream.snapshot(cState, (event, _state: World) => {
    updateWorld(_state, WIDTH, HEIGHT)
  }))

  eventLoop.listen(_state => {
    prettyPrint(_state)
    //println(_state)
  })

  def prettyPrint(world: World) {
    for (x <- 0 until world.length) {
      var toPrint = 0
      if (world(x)) toPrint = 1
      print(toPrint + " ")
      if ((x + 1) % WIDTH == 0) { println() }
    }
    println("\n")
  }

  def createWorld() = {
    val newWorld: World = Array.ofDim(WIDTH * HEIGHT)
    var initial = Array.ofDim[Int](WIDTH, HEIGHT)
    initial(1)(0) = 1
    initial(1)(1) = 1
    initial(1)(2) = 1

    for (y <- 0 until initial.length) {
      for (x <- 0 until initial(0).length) {
        if (y < HEIGHT && x < WIDTH) {
          newWorld(y * WIDTH + x) = initial(y)(x) == 1;
        }
      }
    }
    newWorld
  }

  def isAlive(x: Int, y: Int, world: World, width: Int, height: Int) = {
    x >= 0 && y >= 0 && x < width && y < height && world(y * width + x)
  }

  def getNumberOfNeighbours(
      x: Int,
      y: Int,
      world: World,
      width: Int,
      height: Int
  ) = {
    var total = 0

    if (isAlive(x, y + 1, world, width, height)) total = total + 1
    if (isAlive(x - 1, y + 1, world, width, height)) total = total + 1
    if (isAlive(x + 1, y + 1, world, width, height)) total = total + 1
    if (isAlive(x, y - 1, world, width, height)) total = total + 1
    if (isAlive(x - 1, y - 1, world, width, height)) total = total + 1
    if (isAlive(x + 1, y - 1, world, width, height)) total = total + 1
    if (isAlive(x - 1, y, world, width, height)) total = total + 1
    if (isAlive(x + 1, y, world, width, height)) total = total + 1

    total
  }

  def updateCellState(
      x: Int,
      y: Int,
      world: World,
      width: Int,
      height: Int
  ): Boolean = {
    val numberOfNeighbours = getNumberOfNeighbours(x, y, world, width, height)
    if (numberOfNeighbours < 2 || numberOfNeighbours > 3)
      return false
    else if (numberOfNeighbours == 3) {
      return true;
    }
    return world(y * width + x)
  }

  def updateWorld(world: World, width: Int, height: Int): World = {
    /* make a copy of world to modify */
    var newWorld = world.clone()

    for (y <- 0 until height) {
      for (x <- 0 until width) {
        newWorld(y * width + x) = updateCellState(x, y, world, height, width);
      }
    }

    newWorld
  }

  def toggleCell(world: World, x: Int, y: Int, width: Int, height: Int) = {
    var newWorld = world.clone();
    newWorld(y * width + x) = !world(y * width + x);
    newWorld
  }

}
