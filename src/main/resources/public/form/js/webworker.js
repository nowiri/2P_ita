//haciendo la referencia al workers
this.addEventListener('message', function(e) {

    //la información la tenemos en la da.
    var data = e.data;
    var forms = data.stg;
    var ws;
    conectar();

    switch (data.cmd) {
        case 'sincronizar':
            console.log("Inicio de sincronización . . .");

            try {

                sendMessage(forms.length)

                //webSocket.close();
                console.log("Sincronizado con exito!");
            }
            catch(err) {
                console.log("Error al sincronizar"+err);
                webSocket.close();
            }

            this.close(); // Termina el worker.
            break;
        default:

    };

    function conectar() {
        webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/sincronizarForms");
        webSocket.onopen  = function(e){ console.log("Conectado - status "+this.readyState); };
        webSocket.onclose = function(e){
            console.log("Desconectado - status "+this.readyState);
        };
    }

        function sendMessage(msg){
            // Wait until the state of the socket is not ready and send the message when it is...
            waitForSocketConnection(ws, function(){
                console.log("message sent!!!");
                ws.send(msg);
            });
        }

        // Make the function wait until the connection is made...
        function waitForSocketConnection(socket, callback){
            setTimeout(
                function () {
                    if (socket.readyState === 1) {
                        console.log("Connection is made")
                        if (callback != null){
                            callback();
                        }
                    } else {
                        console.log("wait for connection...")
                        waitForSocketConnection(socket, callback);
                    }

                }, 5); // wait 5 milisecond for the connection...
        }
    }
    ,false)

