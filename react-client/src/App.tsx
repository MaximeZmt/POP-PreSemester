import React from 'react';
import logo from './logo.svg';
import './App.css';


var client: WebSocket;


function connect(){

  // Create the WebSocket element
  client = new WebSocket((document.getElementById("inputAddr") as HTMLInputElement).value);

  if (client != null) {
    // Listener for onOpen connection event
    client.onopen = function (event) {
      (document.getElementById("exchange") as HTMLElement ).innerHTML = "";
      (document.getElementById("statusTxt") as HTMLElement ).innerHTML = "Connected";
      (document.getElementById("inputTxt") as HTMLInputElement).disabled = false;
      (document.getElementById("buttonNumb") as HTMLInputElement).disabled = false;
    }

    // Listener for onError
    client.onerror = function (event) {
      (document.getElementById("statusTxt") as HTMLElement ).innerHTML = "Disconnected";
      (document.getElementById("inputTxt") as HTMLInputElement).disabled = true;
      (document.getElementById("buttonNumb") as HTMLInputElement).disabled = true;
      (document.getElementById("exchange") as HTMLElement ).innerHTML = "";
    }

    // Listener for onClose
    client.onclose = function (event) {
      (document.getElementById("statusTxt") as HTMLElement ).innerHTML = "Disconnected";
      (document.getElementById("inputTxt") as HTMLInputElement).disabled = true;
      (document.getElementById("buttonNumb") as HTMLInputElement).disabled = true;
      (document.getElementById("exchange") as HTMLElement ).innerHTML = "";
    }

    // Listener when recieve a Message
    client.onmessage = function (event) {
      console.log("Recieve: "+event.data);
      (document.getElementById("exchange") as HTMLElement ).innerHTML = (document.getElementById("exchange") as HTMLElement ).innerHTML + "<br>" +event.data +"<=(Serv)";
    }
  }

}


function send(){
  var value = (document.getElementById("inputTxt") as HTMLInputElement).value;
  client.send(value);
  console.log("Send Value: "+value);
  (document.getElementById("exchange") as HTMLElement ).innerHTML = (document.getElementById("exchange") as HTMLElement ).innerHTML + "<br>" + "(client)=>" + value
}

// Initially lock some field
window.onload = function(){
  (document.getElementById("inputTxt") as HTMLInputElement).disabled = true;
  (document.getElementById("buttonNumb") as HTMLInputElement).disabled = true;
  (document.getElementById("inputTxt") as HTMLInputElement).value = "";
}

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Welcome to React Socket Client!
        </p>
        <div>
          <fieldset>
          <legend>Connection</legend>
          Status: <span id="statusTxt">Disconnected</span><br></br>
          <input type="text" id="inputAddr" placeholder="Enter Address"></input>
          <button onClick={connect}>Connect</button>
          </fieldset>
          <fieldset>
          <legend>Communication With Server</legend>
          <input type="text" id="inputTxt" placeholder="Enter a Number"></input>
          <button onClick={send} id="buttonNumb">Send Number</button>
          </fieldset>
        </div>
        <div id="exchange">
        </div>
      </header>
    </div>
  );
}

export default App;
