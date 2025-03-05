# Logic

## Strategy
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

## Conflict Resolution
Longest Blockchain wins
If multiple with same length, lowest last hash is selected

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

### GET /command/verify-longest-chain
Query address book and update if longest chain (most work done)
If multiple with same, take the one with the lowest last hash

### GET /command/add-block/<block-msg>
Tell this node to generate a block with this message

### GET /command/kill
Shut this node down

### GET /command/query-addresses
1. Naive: Redo Steps 1-2
2. Graph: Verify still has at least three contact points