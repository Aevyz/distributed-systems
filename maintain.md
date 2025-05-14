# Address Book Maintenance

By default, we use...

- AddressMaintenanceStrategyEnum.NoGreaterThirdDegree,
- AddressMaintenanceStrategyEnum.SubgraphConnection,
- AddressMaintenanceStrategyEnum.BackupToFile,
- AddressMaintenanceStrategyEnum.RemoveUnresponsiveNodes

## Naive Strategy (Deprecated)

> Done at boot

1. Each node starts with a few inbuilt bootstrap nodes
2. Query each bootstrap node for their address list
3. Add themselves to each bootstrap node's address list
4. For each node newly discovered:
    1. Add to own address list
    2. Add self to their address list
    3. Query each new node for their address list
5. Repeat 4 until no new nodes are discovered

## Subgraph Based

> Done at boot and every 60s

1. Each node starts with a few inbuilt bootstrap nodes
2. Query addresses like naive strategy, however cut off at depth of 3 or 100 addresses total found
3. Identify all disjoint subgraphs
4. In each subgraph, add three random nodes into your address book

The idea is, is that you only send messages to those three nodes, who propagate the message within that subgraph.
If a node goes down, you have two other nodes to communicate with. Ideally you can connect to another node in that
subgraph before any further failures happen.

## Third Degree

> Done at boot and every 60s

To prevent long jumps in communication, we have an ideal jump count of 3.
In practice, nodes will attempt to create "short cuts" to cut down on the number of hops needed.

Remember that this query is done

1. Identify any nodes that are three or more hops away
2. Select one of those nodes randomly
3. Add that node to the address book

## Remove Unresponsive Nodes

If a node does not reply within a given time frame (see `Constants`), it will be removed from the address list.
This means that subgraph based maintenance or third degree maintenance will kick in,
creating an alternative connection so that resilience is not impacted.

Prevents sending messages to non-existent nodes, clogging up bandwidth.

## Backup to File

Saves the address book to file. This can be loaded in on boot if desired (see main README's bonus section).
