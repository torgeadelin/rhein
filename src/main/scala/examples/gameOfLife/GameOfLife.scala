package examples.gameOfLife

import rhein.Behaviour
import rhein._
import rhein.ui.{Listing, Button}
import scalatags.JsDom.all._
import scala.scalajs.js._
import org.scalajs.dom
import scala.collection.mutable.ListBuffer
import scalatags.JsDom
import scala.io.Source

/**
  * Class representing Game Of Life
  * To run the game, please create a GameOfLife Object and
  * call the run() method
  *
  */
class GameOfLife {
  type World = ListBuffer[Boolean]

  var pattern1 = ListBuffer(
    ListBuffer(0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0),
    ListBuffer(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0),
    ListBuffer(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ListBuffer(0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1),
    ListBuffer(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ListBuffer(0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0)
  )

  var pattern2 = ListBuffer(
    ListBuffer(1, 1, 0, 0),
    ListBuffer(1, 1, 0, 0),
    ListBuffer(0, 0, 1, 1),
    ListBuffer(0, 0, 1, 1)
  )

  // The speed of transition between states
  val INTERVAL = 500L
  var WIDTH: Int = 0
  var HEIGHT: Int = 0

  // Run method that starts the game of life
  def run(initialPattern: ListBuffer[ListBuffer[Int]]) {
    // Configuration variables
    WIDTH = pattern2.length
    HEIGHT = pattern2(0).length

    // Event stream that emits a one Unit event at interval specified
    var tickStream: Event[Unit] = Event.interval(INTERVAL, INTERVAL)

    // UI pause button
    var buttonPause: Button = new Button("Pause")
    // Holds the state paused or not
    var pauseLoop: EventLoop[Boolean] = new EventLoop()
    var pauseState = pauseLoop.hold(true)
    pauseLoop.loop(
      buttonPause.eventClicked
        .map(click => true)
        .snapshot(pauseState, (event, _pauseState: Boolean) => {
          !_pauseState
        })
    )

    // Filterin out tick events when we are in pause state
    tickStream = tickStream.filter(x => pauseState.sampleNoTrans())

    /**
      * Holds the state of the world
      * The datatype of EventLoop is List[World] but it could just be World.
      * The decision of wrapping the World in a List is to facilitate the use
      * of Listing UI Component
      */
    var eventLoop: EventLoop[List[World]] = new EventLoop()
    var cState = eventLoop.hold(List(createWorld(pattern2)))
    eventLoop.loop(tickStream.snapshot(cState, (event, _state: List[World]) => {
      List(updateWorld(_state.head, WIDTH, HEIGHT))
    }))

    // UI component for the grid - it holds the current state of the world
    // and maps the world to a DOM table/grid with the provided function
    var grid: Listing[World] =
      new Listing(cState, (world: World, index: Int) => {
        makeGrid(world)
      })

    // Appending all UI components to the DOM
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Game of Life"),
        grid.domElement,
        buttonPause.domElement
      ).render
    )
  }

  /**
    * UI Component representing a single cell
    * If cell is alive, returns a black square, otherwise white square
    * @param alive
    * @return td HTML fragment
    */
  def cellTd(alive: Boolean) = {
    var color = if (alive) "#000" else "#fff"
    td(
      style := s"width: 15px; height: 15px; border: 0.5px solid #ccc; background-color: ${color};"
    )
  }

  /**
    * UI Component representing the whole World / grid
    * It maps through all cells in the World and
    * creates a square according to the cell state
    * @param world
    * @return table HTML fragment
    */
  def makeGrid(world: World) = {
    table(style := "border-collapse: collapse")(
      tbody(
        world
          .sliding(HEIGHT, HEIGHT)
          .toList
          .map((row: ListBuffer[Boolean]) => {
            tr(row.map((cell: Boolean) => {
              cellTd(cell)
            }))
          })
      )
    )
  }

  /**
    * Generate World state given an initial pattern / configuration for
    * the map / world
    * @param initial  Initial pattern
    * @return World state with initial pattern
    */
  def createWorld(initial: ListBuffer[ListBuffer[Int]]) = {
    val newWorld: World = ListBuffer.fill(WIDTH * HEIGHT)(false)

    for (y <- 0 until initial.length) {
      for (x <- 0 until initial(0).length) {
        if (y < HEIGHT && x < WIDTH) {
          newWorld(y * WIDTH + x) = (initial(y)(x) == 1);
        }
      }
    }
    newWorld
  }

  /**
    * Returns true if cell at position x, y is alive, false otherwise
    *
    * @param x
    * @param y
    * @param world
    * @param width
    * @param height
    * @return Cell's' current state
    */
  def isAlive(x: Int, y: Int, world: World, width: Int, height: Int) = {
    x >= 0 && y >= 0 && x < width && y < height && world(y * width + x)
  }

  /**
    * Returns number of neighbours around a cell at position x y
    *
    * @param x
    * @param y
    * @param world
    * @param width
    * @param height
    * @return Nb of neighbours
    */
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

  /**
    * Update cell state at position x y
    * given the standard game of life rules
    *
    * @param x
    * @param y
    * @param world
    * @param width
    * @param height
    * @return Updated world with cell at x, y changed
    */
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

  /**
    * Updates all cells in the world
    *
    * @param world
    * @param width
    * @param height
    * @return New state for the world
    */
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
  // def toggleCell(world: World, x: Int, y: Int, width: Int, height: Int) = {
  //   var newWorld = world.clone();
  //   newWorld(y * width + x) = !world(y * width + x);
  //   newWorld
  // }

}
