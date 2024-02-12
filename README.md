# Websocket Pong

## Build
You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

### What/why
- Just wanted to play with websockets and http4s.
- Pong seemed small enough but interesting enough to work on

### TODO
Right now the only thing that works is sending keypresses around to everyone that connects
- Design world state
  - design diffs to send smaller payloads to clients
- UI Tooling, maybe Typescript w/ pixi.js?
- Determine max number of players and figure out when/how to shard games
