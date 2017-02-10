/******************************************************************************

  Messages.scala

  Description:
    Defines actor messages used in simulation

  Scala 2.10

  Author: David Sharpe

******************************************************************************/
package PushSumAggregationSimulation



///////////////////////////////////////////////////////////////////////////////
//Define actor messages
sealed trait Messages
case class StartSimulation(numNodes: Int, topology: String) extends Messages
case class PushSumMsg(s: Double, w: Double) extends Messages
case class ActorConverged(id: Int) extends Messages

