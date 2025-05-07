package dev.lochert.ds.blockchain

import dev.lochert.ds.blockchain.address.Address
import java.security.SecureRandom
import kotlin.random.Random

object Constants {

    var DIFFICULTY = 4

    const val HASH_ALGORITHM = "SHA3-512"


    var initialAddressSet = setOf(
        Address(getOwnIpAddress(), 8080U), // Localhost Initial
        Address("172.18.0.2", 8080U), // Docker Initial
    )

    var addressStrategy = AddressStrategyEnum.Subgraph

    // Address Search goes 4 deep or terminates at 20 connections
    // Minimum of 3 needed
    var subgraphMaxDepth = 4
    var subgraphMaxSearch = 20

    // Connect at 3 points
    var subgraphConnectionPoints = 3

    var maintainIntervalSeconds: Long = 60
    var maintainAddressStrategies = setOf(
        AddressMaintenanceStrategyEnum.NoGreaterThirdDegree,
        AddressMaintenanceStrategyEnum.SubgraphConnection,
        AddressMaintenanceStrategyEnum.BackupToFile,
        AddressMaintenanceStrategyEnum.RemoveUnresponsiveNodes
    )
    var maintainBlockchain: BlockchainMaintenanceStrategyEnum? =
        BlockchainMaintenanceStrategyEnum.ImmediateLongestLowestHash
    var enableMaintainLoop = true
    var readAddressBook = false

    var setRandom = Random(SecureRandom().nextInt())

    const val miningReward = 1000.0
    const val defaultTransactionFee = 2


}

