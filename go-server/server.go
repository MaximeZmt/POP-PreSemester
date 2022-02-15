package main

import (
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"strconv"
)

const Domain = "localhost"
const Port = "8080"

//var addr = flag.String("addr", (Domain + ":" + Port), "http service address")

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func homePage(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Hello World Home Page !")
}

func reader(conn *websocket.Conn) {
	var counter = 0
	for {
		messageType, p, err := conn.ReadMessage()

		if err != nil {
			log.Println(err)
			return
		}

		log.Println(string(p))

		intVar, err := strconv.Atoi(string(p))
		counter += intVar
		log.Println(counter)

		if err != nil {
			log.Println(err)
			if err = conn.WriteMessage(messageType, []byte("Not A Number")); err != nil {
				log.Println(err)
				return
			}
		}

		if err = conn.WriteMessage(messageType, []byte(string(counter))); err != nil {
			log.Println(err)
			return
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
	log.Println("Client Successfully Connected")
	reader(ws)

}

func setupRoutes() {
	http.HandleFunc("/", homePage)
	http.HandleFunc("/ws", wsEndpoint)
}

func main() {
	fmt.Println("Go Websockets")
	setupRoutes()
	log.Fatal(http.ListenAndServe((Domain + ":" + Port), nil))
}
