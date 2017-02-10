/******************************************************************************

  Topology.scala

  Description:
    Defines different connections for various network topologies 

  Scala 2.10

  Author: David Sharpe

******************************************************************************/
package PushSumAggregationSimulation


import scala.math._


///////////////////////////////////////////////////////////////////////////////
//Class to create topology mapping and get valid neighbors for a specified node
//given a specified topology configuration
class TopologyMapping(_numNodes: Int, _topology: String) {

  val topology = _topology
  val numNodes: Int = _numNodes

  val rand = new scala.util.Random


  def GetMeshNeighbors(id: Int, isImp: Boolean) = {

    //Create a square grid big enough to hold the number of nodes
    //The number of nodes does not need be a perfect square however
    //If not a perfect square number of nodes then a portion of the
    //square grid simply won't be populated
    val size = ceil(sqrt(numNodes)).toInt

    //Empty list of neighbors to start
    var tempList = List[Int]()

    //current row number in grid
    val row = (id / size) + 1

    //If not the bottom row or the node below is not a valid node then add the node below
    if ((row != size) && ((id + size) < numNodes)) {
      tempList ::= (id + size)
    }

    //If not the rightmost column and not the last node then add the node to the right
    if ((id != (row * size) - 1) && ((id + 1) < numNodes)) {
      tempList ::= (id + 1)
    }

    //If not the leftmost column then add the node to the left
    if (id != (row - 1) * size) {
      tempList ::= (id - 1)
    }

    //If not the top row then add the node above
    if (row != 1) {
      tempList ::= (id - size)
    }

    //Should we add a random node?
    if(isImp == true)
    {
      //Pick a random node
      var randNode = rand.nextInt(numNodes)

      //Make sure it is not already a neighbor or our own ID. If so keep picking until another is found
      while(tempList.contains(randNode) == true || randNode == id) {
        randNode = rand.nextInt(numNodes)
      }

      //Add the random node to the list
      tempList ::= randNode
    }

    //Return the list of neighbor node IDs for this node
    tempList
  }


  //Method to get a random neighbor node ID for a specified node
  def GetRandomNeighbor(id: Int) : Int = {
    var randomNeighbor: Int = 0

    //Builds the lists of neighbor IDs for a given topology
    topology match {
      case "full" =>
        //Full topology has all other nodes connected except self
        randomNeighbor = rand.nextInt(numNodes)
        while (randomNeighbor == id) //don't allow neighbor to be our own ID
        {
          randomNeighbor = rand.nextInt(numNodes)
        }

      case "line" =>
        //Line topology has next and previous node connected except at endpoints
        if(id == 0) randomNeighbor = 1 //start of line
        else if(id == numNodes-1) randomNeighbor = numNodes-2 //end of line
        else { //nodes in middle of line
          val x = rand.nextInt(2) //flip a coin to decide which of two neighbors to choose
          if(x == 0) randomNeighbor = id-1
          else randomNeighbor = id+1
        }

      case "2D" =>
        //Simple 2D mesh
        val neighbors = GetMeshNeighbors(id, false)
        randomNeighbor = neighbors(rand.nextInt(neighbors.length))

      case "imp2D" =>
        //2D mesh with an extra random node connected to each node
        val neighbors = GetMeshNeighbors(id, true)
        randomNeighbor = neighbors(rand.nextInt(neighbors.length))
    }
    randomNeighbor
  }
}
