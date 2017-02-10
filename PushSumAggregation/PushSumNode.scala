/******************************************************************************

  PushSumActor.scala

  Description:
    Actor that simulates a network node for push sum aggregation simulation

  Scala 2.10

  Author: David Sharpe

******************************************************************************/
package PushSumAggregationSimulation


import akka.actor._
import scala.math._


///////////////////////////////////////////////////////////////////////////////
//Actor simulating network node Push-Sum simulations
class PushSumNode(id: Int, topology: TopologyMapping) extends Actor {

  //println(self.path.name + " started")

  //If an actors ratio s/w did not change more than this value
  //in 3 consecutive rounds then the actor terminates.
  val terminationThreshold = pow(10, -10)

  //Each node maintains two quantities: s and w. Initially, s = xi = i
  //(that is node number i has value i)
  //w=1 for all nodes at start of simulation performs an average
  //w=1 for a single node at start of simulation performs a sum
  var s        :Double = id
  var w        :Double = 1
  var estimate :Double = 0 //estimate
  var previous :Double = 0 //previous estimate
  var count = 0

  //Receive actor messages
  def receive = {

    case PushSumMsg(s_in, w_in) => {

      //Upon receive, an actor should add received pair to its own corresponding values.
      s += s_in
      w += w_in

      //Upon receive, each actor selects a random neighbor and sends it a message.
      //When sending a message to another actor, half of s and w is kept
      //by the sending actor and half is placed in the message.
      s = s / 2
      w = w / 2
      val randomId = topology.GetRandomNeighbor(id)
      context.actorSelection("../PushSumActor:" + randomId) ! PushSumMsg(s, w)

      //At any given moment of time, the sum estimate is s/w
      //where s and w are the current values of an actor.
      estimate = s / w
      //println(self.path.name + " Estimate = " + estimate)

      //If an actors ratio s/w did not change more than threshold in
      //3 consecutive rounds the actor terminates.
      if((previous - estimate).abs < terminationThreshold) {
        count += 1
        if(count == 3) {
          println(self.path.name + " Done...Estimate = " + estimate)
          context.parent ! ActorConverged(id)
        }
      }
      previous = estimate
    }

    case _ => println(self.path.name + " received unknown message")
  }
}