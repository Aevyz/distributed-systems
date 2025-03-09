# Strategies

| Name             | Initialize                                                                                                                                                        | Maintain                                                                                       |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| Naive            | Connect to all central nodes; query for all their known nodes; register for all                                                                                   | Repeat query for random sample                                                                 |
| Chain Based      | Connect to all central nodes; query all for their known nodes; continue querying; register at each initial node; register at 4 points in each disjointed subgraph | Verify still attached to 4 point in each disjoint subgraph (more is ok); ping the nodes nearby |
| Prioritized List | Connect to all central nodes; query all for their known nodes; register at all;                                                                                   | Nodes that send more traffic are prioritized, send traffic prioritized to least common         |

# Finding Blocks
1. Query every connected node every x seconds
2. If new block, send to connected nodes in Address Book