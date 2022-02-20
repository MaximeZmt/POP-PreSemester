package main

import (
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"strconv"
)

// CHANGE THE CONST HERE TO MODIFY THE DOMAIN AND/OR THE PORT
const Domain = "localhost"
const Port = "8080"

// Initialize the Upgrader with the buffer for WebSocket communication
var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func homePage(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Non Socket Connection")
}

/*
  Function That is instantiated for each client
*/
func reader(conn *websocket.Conn) {
	var counter = 0 // init the counter at 0
	for {
		messageType, p, err := conn.ReadMessage()

		if err != nil {
			// If there is an error while reading the message
			log.Println(err)
			return
		}

		log.Println("Recv: ", string(p))

		intVar, err := strconv.Atoi(string(p))
		counter += intVar

		if err != nil {
			log.Println("Err: ", err)
			log.Println("Err: NotANumber")
			// If error while converting message received from string to int, then reply NotANumber
			if err = conn.WriteMessage(messageType, []byte("NotANumber")); err != nil {
				log.Println("Err: ", err)
				return
			}
		} else if err = conn.WriteMessage(messageType, []byte(strconv.Itoa(counter))); err != nil {
			log.Println("Err: ", err)
			return
		} else {
			log.Println("Send: ", counter)
		}

	}
}

func wsEndpoint(w http.ResponseWriter, r *http.Request) {
	//Way (dirty and quick) acccept any sockets into endpoint regardless of origin
	upgrader.CheckOrigin = func(r *http.Request) bool { return true }

	ws, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print(err)
	}

	// New Client connected
	log.Println("Client Successfully Connected")
	reader(ws)
}

func setupRoutes() {
	http.HandleFunc("/", homePage)     // Non WebSocket
	http.HandleFunc("/ws", wsEndpoint) // Websocket
}

func main() {
	fmt.Println("Golang Websockets Server")
	fmt.Println("Connection at: ws://" + (Domain + ":" + Port) + "/ws")
	setupRoutes()
	log.Fatal(http.ListenAndServe((Domain + ":" + Port), nil))
}
