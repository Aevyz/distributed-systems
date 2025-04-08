# Distributed Systems: Blockchain Project

## Dependencies

- Java
- Kotlin

### Maven Dependencies

- org.jetbrains.kotlinx:kotlinx-serialization-json
- org.jetbrains.kotlinx:kotlinx-coroutines-core

> There may be other graphical dependencies, which were used to attempt to have a graphical interface.
> If these end up being used productively, they will be listed here.

Important: When building disable Unit Tests. These tests have long life spans in order to simulate a network.

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

## Reading Previous Address Books

Enable reading of previous address books in Constants. Address books are saved to
`"/tmp/address/${ownAddress.ip}.${ownAddress.port}.addressbackup"`

## Address Strategy
### Naive Strategy
1. Connect to each core node to find addresses
2. Continue to query addresses until all nodes identified (cut off of point 3 queries deep Or 100 addresses total)
3. New blocks are propagated to all in address book

### Graph Based
1. Connect to each core node to find addresses
2. Continue to query addresses until all nodes identified (cut off of point 3 queries deep Or 100 addresses total)
3. Identify disjoint graphs
4. Add the entry point of each chain, plus three other random points into address book
5. Send messages only there
6. If one node goes down, replace with a new node on that chain (query all three remaining nodes and pick a random address)

## Test Cases

## Maintenance

### Consensus (Maintain Blockchain)

#### Blockchain: Longest Chain, Lowest Last Hash (Prototype)

Queries only neighbors for their longest blockchain. If it is the longest, overwrite own.

> Enable in Constants

- Longest Blockchain wins
- If multiple with same length, lowest last hash is selected

`blocks.sortedWith(compareByDescending<BlockChain> {it.listOfBlocks.size}.thenBy { it.listOfBlocks.last().blockHash }).firstOrNull()`

### Address Book

Possible Strategies

- SubgraphConnection
- NoGreaterThirdDegree
- RemoveUnresponsiveNodes
- BackupToFile

#### Subgraph Connection

If less than `subgraphConnectionPoints` (default=3), query graph and at least three points per disjointed subgraph
found. => Improve Resilience

### No Greater Third Degree

Look for all nodes that are three or more jumps away. Pick a random one and add that to address book. => Improve
Resilience

### Remove Unresponsive Nodes

If a node does not respond x times, remove it from the address book. => Prevent dead nodes from clogging up Address Book

### Backup To File

Backup address book into a file. You can load the file at startup.

## Transactions
Each block contains a "owner" and signature
For now, owner is a random string (future: public key)
Each block gives the owner 1000 coins (can be used in transactions)

## Endpoints
### GET /address
All nodes in address book

### POST /address
Add node to address book

### GET /block
All blocks

### POST /block
Add a block to this blockchain

### GET /transactions/all
All transactions not in blocks

### GET /transactions/create
Creates a random transaction

### GET /transactions/create/{sender}/{recipient}/{amount}
Creates a transaction with the given values

### POST /transactions/post
Add a transaction to the transactions list

### GET /command/add-block/<block-msg>
Tell this node to generate a block with this message


## Limitations

IPv4 support is guaranteed. IPv6 will require some manual adjustments to the AddressList in order to work.

# Midterm Presentation TODOs

- README (Done)
  - Add examples
- Transaction Logic (Done)
- Maintenance
  - Add a third level node by default (Done)
  - Conflict resolution? (maybe leave till phase 2) (Done)
- Addressbook to file (Done)

## Mempool proposal (Later)

- Each block can hold max 5tx
- Each tx also contains a fee
- Tx mined will give to miner
- Miner will prioritize highest fee first