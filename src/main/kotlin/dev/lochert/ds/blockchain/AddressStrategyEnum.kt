package dev.lochert.ds.blockchain

enum class AddressStrategyEnum{
    Naive,
    Subgraph,
}

enum class AddressMaintenanceStrategyEnum {
    SubgraphConnection,
    NoGreaterThirdDegree,
    RemoveUnresponsiveNodes,
    BackupToFile
}

enum class BlockchainMaintenanceStrategyEnum {
    ImmediateLongestLowestHash // 1. Longest Chain, 2. Lowest last hash
}