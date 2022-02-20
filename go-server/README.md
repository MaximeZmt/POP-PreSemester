# Go-Server

Welcome to the Golang Server directory of my small WebSocket program !



To configure the Domain and the Port, change the following lines in `server.go`

```go
const Domain = "localhost" // Line 12
const Port = "8080" // Line 13
```



To Run the program, type:

`go run ./server.go`

Then to connect the address is:

`ws://Domain:Port/ws`

![](https://miro.medium.com/max/1000/1*vHUiXvBE0p0fLRwFHZuAYw.gif)

[*Gif Comes from medium.com*](https://medium.com/@kevalpatel2106/why-should-you-learn-go-f607681fad65#.h5izsfczr)



#### Information: 

-  It uses the Gorilla WebSocket library: https://github.com/gorilla/websocket
