/******************************************************************************

  PushSumSimulation.scala

  Description:
    Push Sum Aggregation simulation using Scala and AKKA actors

    Performs simulation using an AKKA actor for each node in the network

    See the paper, "Gossip-Based Computation of Aggregate Information"
    by Kempe", Dobra, and Gehrke for detailed description and proof
    of the algorithm

  Scala 2.10

  Author: David Sharpe

******************************************************************************/
package PushSumAggregationSimulation

import akka.actor._


///////////////////////////////////////////////////////////////////////////////
//SimulationController Actor - Controls simulation and tracks the start and stop time
class SimulationController extends Actor {
  
  var startTime: Long = 0
  var actorsPending: Int = 0
  val rand = new scala.util.Random
  
  def receive = {
    case StartSimulation(numNodes, topology) => {
                
      //Record the number of actors involved in simulation
      actorsPending = numNodes
      
      //Array of actors
      val actors = new Array[ActorRef](numNodes)

      //Create lists of neighbors based on the topology string and number of nodes
      val topoMap = new TopologyMapping(numNodes, topology)

      //Create numNodes actors
      for (i <- 0 until numNodes) {
        actors(i) = context.actorOf(Props(new PushSumNode(i, topoMap)), name = "PushSumActor:" + i)
      }

      //Start the push-sum simulation by sending zero message to single random node
      //This triggers the push sum algorithm in the node but does not affect the s and w values
      startTime = System.currentTimeMillis
      actors(rand.nextInt(numNodes)) ! PushSumMsg(0, 0)
    }
    
    case ActorConverged(id) => {
      //Keep track of when all actor have converged
      //TODO This may not work in the case of an actor failure depending on network topology.
      //TODO Need to handle an actor failure so we are not waiting on an actor infinitely
      if(actorsPending > 0) {
        actorsPending -= 1
        if(actorsPending == 0) {
          //Simulation has converged
          println("Simulation converged in " + (System.currentTimeMillis - startTime) + " milliseconds")
          context.stop(self)
          context.system.shutdown
          println("Done...Exiting")
          sys.exit(0)
        }
      }
    }
    
    case _ => println("Simulation timer actor received unknown message")
  }
}

///////////////////////////////////////////////////////////////////////////////
//Main Application
object PushSumSimulator extends App {
       
  //Make sure enough parameters were passed in on command line
  if(args.length < 2) {
    println("Not enough arguments! Need: {numNodes topology}")
    sys.exit(-1)
  }
  
  //Display the command line arguments for debugging purposes
  //println("Cmd Line Args: " + (args mkString ", "))
  
  //Store the command line inputs
  val numNodes  = args(0).toInt
  val topology  = args(1)

  //Check if topology string is valid
  val validTopologies = List("full", "2D", "line", "imp2D")
  if(validTopologies.contains(topology) == false) {
    println(topology + " is not a valid topology string. Please try again.")
    println("Valid topologies are " + validTopologies)
    sys.exit(-2)
  }
  
  //Create the actor system
  val system = ActorSystem("MainSystem")
  
  //Create an actor to start the simulation and track time to completion for graphing purposes
  val simTimer = system.actorOf(Props[SimulationController], name = "SimulationController")
  
  //Start the simulation
  simTimer ! StartSimulation(numNodes, topology)
}
