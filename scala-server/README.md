# Scala-Server

Welcome to the Scala Server directory of my small WebSocket program !



To configure the Domain and the Port, change the following lines in `src/main/scala/main.scala`

```scala
  val address = "localhost" // Line 22
  val port = 8080 // Line 23
```

To run the program, type:

`sbt run`

Then to connect the address is:

`ws://address:Port/ws`

#### Information: 

-  The server has been built as an SBT project with Scala3.
-  It uses [Scarlet Library](https://github.com/Tinder/Scarlet) for WebSocket.
