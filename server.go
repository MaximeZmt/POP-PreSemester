package main

import (
	"flag"
	"fmt"
	"html/template"
	"log"
	"net/http"

	"github.com/gorilla/websocket"
)

const Domain = "localhost"
const Port = "8080"

var addr = flag.String("addr", (Domain + ":" + Port), "http service address")

var upgrader = websocket.Upgrader{} // use default options

func echo(w http.ResponseWriter, r *http.Request) {
	// Upgrade our raw HTTP connection to a websocket based one
	c, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print("upgrade:", err)
		return
	}
	defer c.Close()

	// The event loop
	for {
		mt, message, err := c.ReadMessage() //mt is message type
		if err != nil {
			log.Println("Error while reading:", err)
			break
		}
		log.Printf("recieved: %s", message)
		err = c.WriteMessage(mt, message)
		if err != nil {
			log.Println("Error while writing:", err)
			break
		}
	}
}

func home(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "IndexPage")
	//homeTemplate.Execute(w, "ws://"+r.Host+"/echo")
}

func main() {
	//flag.Parse()
	log.SetFlags(0)
	http.HandleFunc("/socket", echo)
	http.HandleFunc("/", home)
	log.Fatal(http.ListenAndServe(*addr, nil))
}

var homeTemplate = template.Must(template.New("").Parse(`
<!DOCTYPE html>
<html>
<body>
	Hello World ! (tut tut!)
</body>
</html>
`))
