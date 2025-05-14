# Distributed Systems: Blockchain Project

This is a university project for the class Distributed Systems at Taltech.
This project recreates "most" parts of Bitcoin.

The consensus algorithm used is a modified PoW (static difficulty) that follows the longest blockchain wins strategy.
The protocol will query all nodes in the address list for their blockchain.
If you have two blockchains with the same length, the one with the lowest last hash will win.

`blocks.sortedWith(compareByDescending<BlockChain> {it.listOfBlocks.size}.thenBy { it.listOfBlocks.last().blockHash }).firstOrNull()`

In order to maintain the node's address list, we employ a few different strategies. These are listed in the
`maintain.md` file.

Each block can contain up to 3 transactions. The transactions with the highest mining reward will be mined first.
The mining of blocks is not done automatically, since this would complicate the demo.
One must manually trigger the block. It is recommended to use `/control/add-block/quick-add` for this.

> IMPORTANT: The unit tests are "simulations" of a network, hence will take a long time to run.
> Make sure to disable this before building with Gradle (`./gradlew build -x test --no-daemon --parallel`)

## Dependencies

- Java
- Kotlin

### Maven Dependencies

- org.jetbrains.kotlinx:kotlinx-serialization-json
- org.jetbrains.kotlinx:kotlinx-coroutines-core
- For Address Diagram
  - org.openjfx:javafx-controls
  - org.openjfx:javafx-graphics
  - org.jgrapht:jgrapht-core
  - org.jgrapht:jgrapht-io



## How to Use

### Run Initial Server

Run main of:
`kotlin/dev/lochert/ds/blockchain/http/server/initial/StartServerInitial.kt`

> Since running main methods not defined in Gradle is a pain, it is advised to run these functions using an IDE e.g.
> IntelliJ

### Run Regular Server

Run main of:
`kotlin/dev/lochert/ds/blockchain/http/server/regular/StartServer.kt`

> Since running main methods not defined in Gradle is a pain, it is advised to run these functions using an IDE e.g.
> IntelliJ

### Docker Compose

To test the blockchain's functionality for multi node systems, we used Docker.
Simply run `docker compose up -d` in the main root folder, and the system will start.

## Endpoints

| Endpoint                      | Description GET                                                                             | Description POST                                                                     |
|-------------------------------|---------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `/address`                    | List all addresses connected to this node                                                   | Add a node to the address book (Expected: dev.lochert.ds.blockchain.address.Address) |
| `/address-graph.svg` *        | Show the connections of each node                                                           | —                                                                                    |
| `/block`                      | List of all Blocks                                                                          | Add a block to the blockchain (Expected: dev.lochert.ds.blockchain.block.Block)      |
| `/block/hash`                 | Get block by hash                                                                           | —                                                                                    |
| `/block/index`                | Get block by index                                                                          | —                                                                                    |
| `/block/hash/from`            | Get blocks starting from hash                                                               | —                                                                                    |
| `/balance`                    | Get balance of all users                                                                    | —                                                                                    |
| `/transaction-log` *          | List all blocks and their transactions, also includes the balances and mining rewards       | —                                                                                    |
| `/transactions/all`           | List all transactions                                                                       | —                                                                                    |
| `/transactions/create`        | Create a transaction with randomized content (for debugging/demoing)                        | —                                                                                    |
| `/transactions/post`          | —                                                                                           | Post a transaction to the network                                                    |
| `/transaction-log`            | Get transaction logs                                                                        | —                                                                                    |
| `/control/add-block/$content` | Adds a block with the content specified in the URL (automatically propagated)               | —                                                                                    |
| `/control/init-mine`          | For each user in the RSA Key Pair database, mine one block (i.e. everyone gets 1000 tokens) | —                                                                                    |

\* Helpful visualizations to be used for demos or debugging

Additionally, it may be helpful to visualize using `test-ui.html` in the resources folder.

## Demos

See the homepage of the node for the normal demos.

### Malicious Demo

1. Start Docker Compose network
2. Init Mine so that everyone has some tokens
3. /control/mallory-tx -> Transaction where Mallory sends Alice some tokens
4. /control/add-block/block -> Add a block to commit transaction
5. Start dev.lochert.ds.blockchain.http.server.malicious.MaliciousEntrypoint

The malicious entrypoint mines 30 blocks. Because 30 blocks is greater than the other network's blockchain,
it will eventually overwrite the other blockchain, meaning that the Mallory to Alice transaction is reversed.

## Bonus Features

### Reading Previous Address Books

Enable reading of previous address books in Constants. Address books are saved to
`"/tmp/address/${ownAddress.ip}.${ownAddress.port}.addressbackup"`

## Limitations

IPv4 support is guaranteed. IPv6 will require some manual adjustments to the AddressList in order to work.


## Code Structure
```
├── main
│   ├── kotlin
│   │   └── dev
│   │       └── lochert
│   │           └── ds
│   │               └── blockchain
│   │                   ├── address # Objects used to hold addresses of other nodes
│   │                   │   ├── Address.kt
│   │                   │   └── AddressList.kt
│   │                   ├── AddressStrategyEnum.kt
│   │                   ├── block # Objects used to create blocks
│   │                   │   ├── balance # Objects used to calculate balance
│   │                   │   │   ├── BalanceEntry.kt
│   │                   │   │   └── Balance.kt
│   │                   │   ├── BlockChain.kt
│   │                   │   ├── Block.kt
│   │                   │   ├── BlockProposal.kt
│   │                   │   └── BlockUtils.kt
│   │                   ├── Constants.kt # Config file
│   │                   ├── ExtensionFunctrions.kt # Helpful extensions
│   │                   ├── http
│   │                   │   ├── handlers # Here is the code for each Server Handler
│   │                   │   │   ├── AddressGraphHandler.kt
│   │                   │   │   ├── AddressHandler.kt
│   │                   │   │   ├── BalanceHandler.kt
│   │                   │   │   ├── BlockHandlerHash.kt
│   │                   │   │   ├── BlockHandlerIndex.kt
│   │                   │   │   ├── BlockHandler.kt
│   │                   │   │   ├── BlocksHandlerHash.kt
│   │                   │   │   ├── ControlAddHandler.kt
│   │                   │   │   ├── TransactionLogsHandler.kt
│   │                   │   │   └── TransactionsHandler.kt
│   │                   │   ├── HttpUtil.kt # Util Function to make POST and GET easier
│   │                   │   ├── Message.kt # Error Messaging Helper
│   │                   │   └── server 
│   │                   │       ├── DockerInit.kt # Starting point for Docker
│   │                   │       ├── initial
│   │                   │       │   └── StartServerInitial.kt # Starting point for new network
│   │                   │       ├── malicious
│   │                   │       │   ├── MaliciousEntrypoint.kt # Starting point for malicious node demo
│   │                   │       ├── regular
│   │                   │       │   └── StartServer.kt # Starting point for regular node
│   │                   │       ├── Server.kt # Main code base for server
│   │                   │       └── strategy
│   │                   │           ├── address # see strategy.md
│   │                   │           │   ├── naive
│   │                   │           │   │   └── NaiveStrategy.kt # Deprecated
│   │                   │           │   └── subgraph # Create a graph of the network
│   │                   │           │       ├── Graph.kt
│   │                   │           │       ├── LastGraph.kt
│   │                   │           │       ├── Node.kt
│   │                   │           │       ├── SubgraphStrategy.kt
│   │                   │           │       └── Visualization.kt
│   │                   │           └── maintenance # Implementation of the maintenance loops
│   │                   │               ├── BackupAddressMaintenance.kt
│   │                   │               ├── ConnectionPointMaintenance.kt
│   │                   │               ├── ImmediateLongestBlockchainLowestHash.kt
│   │                   │               ├── Maintenance.kt
│   │                   │               ├── NoThreeJumpsMaintenance.kt
│   │                   │               └── RemoveInactiveMaintenance.kt
│   │                   ├── pki # Encryption and Signature class
│   │                   │   ├── RSAKeyPair.kt
│   │                   │   └── RSAKeyPairs.kt
│   │                   └── Transactions # Objects for Transaction Logic
│   │                       ├── Transaction.kt
│   │                       └── Transactions.kt
│   └── resources
│       ├── rsa # Here be the public and private keys
│       │   ├── alice_private.key
│       │   ├── alice_public.key
│       │   ├── bob_private.key
│       │   ├── bob_public.key
│       │   ├── carol_private.key
│       │   ├── carol_public.key
│       │   ├── dan_private.key
│       │   ├── dan_public.key
│       │   ├── frank_private.key
│       │   ├── frank_public.key
│       │   ├── george_private.key
│       │   ├── george_public.key
│       │   ├── mallory_private.key
│       │   └── mallory_public.key
│       └── testui.html

```