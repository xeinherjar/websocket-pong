const socket = new WebSocket("ws://localhost:8000/ws");
const interval = setInterval(() => { console.log("sending"); socket.send("hello from client") }, 10000)

document.addEventListener("keydown", (event) => {
  const keyCode = event.keyCode
  const key = event.key

  //socket.send(`[Client]: Key: ${key}, keyCode: ${keyCode}`)
  socket.send(keyCode)
});

socket.onmessage = function (event) {
  console.log(event.data);
};

socket.onclose = function (event) {
  console.log("WS closed");
  clearInterval(interval)
}; 

