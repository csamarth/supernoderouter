# Supernode Routing : A grid-based message passing scheme for sparse opportunistic networks

[Subham Kumaram](https://github.com/shubhamkrm) and I proposed a novel routing algorithm suitable for sparse networks in scenarios such as cities connected to each other through bus routes or troops stationed in different units bridged by the help of runners. This original research work was published at the peer-reviewed international journal, [The Journal of Ambient Intelligence and Humanized Computing, Springer](https://www.springer.com/journal/12652/about) and can be found [here](https://doi.org/10.1007/s12652-018-0993-y).

# Index

* [Terminologies](#terminologies)
* [Description of Work](#description-of-work)
* [Results](#results)
* [About the Simulator](#about-the-simulator)

# Terminologies

## Normal Nodes

These nodes constitute the majority of the network. Normal nodes are uniformly distributed in each cell. They move within a cell and communicate only with other nodes in the same cell. They can be any mobile device which can communicate with other devices.

## Supernodes

These are the nodes which are used to transmit messages from one cell to another. These nodes generally move near the vertices of the cells. Since they are used for inter-cell transmission, they are rarer and can be given greater resources. Hence, these nodes typically have higher energy, buffer size and radio range.

## SNAP

SNAPs or SuperNode Address Packets are broadcast throughout the network when a new node joins the network. The SNAPs consists of the node id, it’s predicted movement area defined as four coordinates of sides of a rectangle, energy and transmission range. 

# Description of Work

Opportunistic Networks (Oppnets) are generally formed by mobile nodes with limited memory and battery life, hence minimizing overhead and conserving energy is of immense importance. Many existing protocols target this problem by controlling the number of messages in the network by utilising topological or contextual information. However, in doing so, memory is wasted in storing this contextual information as well as a decline in the number of messages delivered is noted. Another critical factor that severely affects oppnets is the sparseness between nodes. The existing protocols assume a network in which all the nodes move within a common region. While this assumption works when the nodes all belong to the same city, it fails when the nodes are grouped into different locations with limited sources of communications between the two. Hence, in this work, an effort has been made to optimize all routing parameters while minimizing resource utilisation. It aims at providing reasonable delivery probability while also keeping the overhead ratio minimal.

Supernode routing, does not rely on any extra information, but only on the location of the various nodes present in the concerned area. Most devices and vehicles (the majority constituents of a Delay Tolerant Network) are equipped with GPS facility which is the only requirement. No characteristic information about the routes of vehicles, map of the area, types of devices participating is involved. Flooding among normal nodes is constrained within small groups, which helps reduce the overhead. Supernodes are used to transmit messages between groups, and with the optimum choice of supernodes, the messages can be relayed along the shortest path through the groups.

To replicate specific sparse scenarios, it is assumed that most of the nodes only move within a fixed area, but some of the nodes (supernodes) have higher connectivity and move among such fixed adjacent areas. Supernode routing intends to breakdown routing over a large area down to smaller cells. This makes the message transfer more manageable. Each cell is placed in a manner to form a grid pattern and is assigned a unique code that distinguishes it from others. These unique cell codes help messages identify the direction they must travel in, from the source cell to the destination. The cells are numbered in a row-column manner. The cell code follows the pattern "C X-X" where C is used to denote a regular cell and the first X indicates the row number and the second X indicates the column number. Suprenodes are more stable and are found towards the corners of these cells where they travel between multiple cells. They are also assigned codes in a manner much similar to that of the cells. They follow a pattern of "S X-X" where S denotes the node as a supernode and the Xs follow the same terminology.

## Forwarding Procedure

This comparison is done from the first time a node meets a new node and is continued at every encounter till the destination is reached. If the two nodes lie within the same cell then the message is transferred through flooding. If the cells differ, calculation is done for the movement of the message through different cells. This comparison is done by subtracting the X and Y coordinate numbers of the cell of the source node from those of the destination node, respectively. This gives us the direction along both, vertical and horizontal axes. This direction is used to select the optimal supernode for transferring the message to the vertices of the current cell. After reaching a supernode, the next target is calculated. When two supernodes are equally suited for routing, as is the case when the current cells and the destination cell lie on the same horizontal or vertical axis, both the supernodes are selected for routing.  This again is based on the direction of the message which is computed every time the cell containing the message changes. The following procedure continues till the supernode on the vertex of the destination cell is reached; after which message is transmitted to the node through flooding.

Through this routing protocol, supernodes at more than one vertex may be selected. This improves the results by creating favourable conditions for a higher delivery chance of messages. Once a message has been transmitted to the next supernode in the routing path, the message is removed from the cell using acknowledgments.

# Results

## Metrics

The metrics used to compare the performance of Supernode model against others are as follows:

1. **Number of messages delivered:** It is the total number of messages successfully delivered.

2. **Overhead ratio:** It is the ratio of the total number of messages relayed in the network to the number of messages actually delivered.

3. **Average latency:** It is the average time taken by a message to be delivered. It is calculated by subtracting the time of creation of message from time of delivery of message.

4. **Average residual energy:** It is the average of the energy left in nodes after completion of the simulation. Here only the normal nodes have been considered, and not the supernodes, since their energy is assumed to be higher than normal nodes and can skew the average.

5. **Number of dead nodes:** It is the number of nodes whose residual energy is too low (≤ 100 units) after the completion of the simulation.

For the calculation of results, different parameters were varied one at a time while keeping other parameters constant to check their effect on the above-discussed metrics and see how Supernode routing protocol fares with its competitors. The different parameters along with their individual results are as follows: 

## Cluster Size

### Number of Messages Delivered

The size of the cluster or the local area within which a group of nodes were limited to was varied so as to increase the gap between two adjacent clusters. When the cluster size is the smallest or when the network is the sparsest, Supernode outperforms MaxProp, Epidemic, PROPHET, ProWait and Grad by 46.15, 237.10, 258.29, 216.67 and 207.35% respectively.

While there is a net decrease in the number of messages delivered as the network gets sparser, the performance of Supernode Router relative to other protocols actually increases. This is because in sparse networks, treating supernodes as intermediate destinations and sending acknowledgments once the message leaves the cell minimises the nodes’ energy consumption, leading to a lesser number of dead nodes. Moreover, the Supernode approach guides the messages more effectively.

### Overhead Ratio

Supernode router achieves remarkably low overhead than the other protocols with ProWait and GRAD as exceptions, which sacrifice inter-cell connectivity in order to keep resource consumption low. Quantitatively, Supernode router achieves 64.57, 86.58, and 84.78% lesser overhead than MaxProp, Epidemic, and PROPHET routers respectively.

### Average Latency

Even though Supernode router achieves higher delivery rates and lower overhead, the latency is comparable to those achieved by other protocols.

### Average Residual Energy and Number of Dead Nodes

With the exception of ProWait and GRAD, Supernode protocol has the highest average residual energy and least number of dead nodes. It is observed that when cluster range is smallest, Supernode router has 205.96, 91.32 and 15.81% greater residual energy than MaxProp, Epidemic, and PROPHET routers respectively.

Similarly, regarding the number of dead nodes, Supernode routing results in 63.35, 50.54 and 47.43% less dead nodes than MaxProp, Epidemic, and PROPHET respectively.

An interesting observation is that while the average energy of nodes in other protocols decreases with an increase in cluster range, in Supernode protocol, the opposite happens. This is because as the cells get closer to each other, direct inter-cell transmissions between normal nodes increase. This leads to an increase in the amount of energy spent. Whereas, in the Supernode protocol, inter-cell communications can only take place through supernodes. Hence, the only way increasing cluster range affects Supernode routing is by decreasing intra-cell network density. This decrease results in lesser number of node encounters, and hence lesser consumption of energy

## Number of Supernodes

### Number of Messages Delivered

Though the number of messages delivered increases with the number of supernodes, it is not as drastic as in the case of MaxProp, which shows that Supernode routing works well even in cases where multiple supernodes can fail. On average, Supernode protocol delivers 39.56, 233.83, 189.67, 246.45 and 233.85% more messages than MaxProp, Epidemic, PROPHET, ProWait and GRAD protocols respectively.

### Overhead Ratio

The overhead ratio is observed to decrease with an increase in the number of supernodes. On average, supernode protocol delivers 67.52, 87.35 and 81.95% lesser overhead than MaxProp, Epidemic, and PROPHET protocols respectively.

### Average Latency

The average latency, in case of Supernode protocol, decreases with an increase in the number of supernodes; whereas, in other protocols, an opposite trend is seen. As the number of supernodes increases, the other protocols naively make more transmissions to the supernodes, resulting in more consumption of energy. This leads to a lot of dead nodes, due to which the number of active nodes decrease and hence latency increases. Overall, the latency in Supernode protocol is comparable to other routing protocols.

### Average Residual Energy

As the number of supernodes increases, there is a higher probability of messages being passed to a supernode at an earlier time. This reduces the amount of flooding, which results in a reduced energy consumption.

It is seen that with the maximum number (5) of supernodes per vertex the average residual energy in Supernode routers is 1356.81, 551.54 and 78.86% higher than MaxProp, Epidemic and PROPHET, respectively.

### Number of Dead Nodes

The number of dead nodes follows a similar trend. It decreases with an increase in the number of supernodes in Supernode routing, whereas it increases in case of other protocols. This effect is highly pronounced in 5 supernodes scenario, where only 71 out of 400 nodes are dead in Supernode protocol, the number of dead nodes in MaxProp, Epidemic, and PROPHET were as high as 349, 316 and 195 respectively under the same conditions.

## Number of Nodes per cell

### Number of Messages Delivered

The message delivery rate increases with an increase in the number of nodes, because with larger node density, more agents are available to carry the message from one supernode to another via flooding. On average, the Supernode protocol delivers 36.03% more messages than MaxProp, 228.53% more messages than Epidemic, 147.93% more messages than PROPHET, 289.09% more messages than ProWait and 315.56% more messages than GRAD.

### Overhead Ratio

As the network density increases, there is an almost linear increase in the overhead, but the rate of this increase is less in comparison to other protocols, except ProWait and GRAD, and even in sparser networks, the overhead is comparable to ProWait. Supernode achieves 70.62, 87.71 and 83.07% lesser overhead than MaxProp, Epidemic, and PROPHET routing protocols respectively when the number of nodes per cell is 30 (the maximum amount).

### Average Latency

Average latency decreases with an increase in network density. The latency in Supernode protocol is comparable to other high-performance protocols and the differences are too low to be of statistical significance.

### Average Residual Energy

It is observed that Supernode routing protocol consistently outperforms MaxProp, PROPHET and Epidemic protocols in this metric. The average residual energy graph for Supernode shows a gradual decline with increasing network density, but the rate of decline is lower than that of MaxProp and Epidemic.

This decline is due to increase in the number of encounters between nodes, which result in higher number of transmissions, but its rate is less than those of MaxProp, Epidemic, and PROPHET due to the less rate of increase of overhead for the Supernode protocol.

### Number of Dead Nodes

While MaxProp, Epidemic and PROPHET protocols show a dramatic increase in the number of dead nodes with an increased network density, the Supernode protocol shows a gradual increase.

This work does not discuss the ProWait and GRAD routers’ performance with respect to overhead and energy consumption because they deliver very few messages, thus their energy consumption is also expected to be less, as is the case shown by the results.

## Buffer Size

### Number of Messages Delivered

At the maximum buffer size (10 MB), Supernode router delivers 16.37% more messages than MaxProp, 227.95% more than Epidemic, 204.38% more than PROPHET, 382.18% more messages than ProWait and 355.14% more than GRAD.

### Overhead Ratio

The overhead ratio decreases almost in an inverse ratio with buffer size, and the results show a marked improvement over other protocols. At 10 MB buffer size, the overhead values are observed to be 80.97, 95.02 and 89.50% lesser than MaxProp, Epidemic and PROPHET respectively.

### Average Latency

The average latency, in this case too, is comparable to the other protocols, and increases with increase in buffer size. This increase is gradual and the average latency was found to be 5572.79 seconds.

### Average Residual Energy and Number of Dead Nodes

While the energy consumption in other models increases with increase in buffer space, the opposite is seen in the Supernode protocol. This may be due to the fact that when the buffer size is too small, more messages are dropped and hence, the same message is received multiple times during flooding, resulting in an increase in the number of transmissions.

At 10 MB buffer size, Supernode routing results in only 64 dead nodes, whereas the number of dead nodes for MaxProp, Epidemic, and PROPHET are 322, 305 and 175, respectively.

# About the Simulator

All the simulations were performed in the Opportunistic Network Environment (ONE) Simulator. 

For introduction and releases, see [the ONE homepage at GitHub](http://akeranen.github.io/the-one/).

For instructions on how to get started, see [the README](https://github.com/akeranen/the-one/wiki/README).

The [wiki page](https://github.com/akeranen/the-one/wiki) has the latest information.

