#Push Sum Aggregation
Simulation of (gossip based) push-sum aggregation using AKKA actors for network nodes. 

Implements a push-sum aggregation simulation using AKKA actors for each network node. See the paper, "Gossip-Based Computation of Aggregate Information" by Kempe", Dobra, and Gehrke for detailed description and proof of the algorithm.

##Instructions to compile/run
Change to the directory with the build.sbt file and execute: 

      sbt "run numberOfNodes topology"
      
   where topology = {full|2D|imp2D|line}

Example:
 
      sbt "run 100 full"

