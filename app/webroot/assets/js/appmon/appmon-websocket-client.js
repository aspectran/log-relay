function AppmonWebsocketClient(endpoint, onEndpointJoined, onEstablishCompleted, onErrorObserved) {
    let socket = null;
    let heartbeatTimer = null;
    let pendingMessages = [];
    let established = false;

    this.start = function () {
        openSocket();
    };

    this.stop = function () {
        closeSocket();
    };

    const openSocket = function () {
        // onErrorObserved(endpoint);
        // return;
        if (socket) {
            socket.close();
        }
        let url = new URL(endpoint.url, location.href);
        url.protocol = url.protocol.replace('https:', 'wss:');
        url.protocol = url.protocol.replace('http:', 'ws:');
        socket = new WebSocket(url.href);
        let self = this;
        socket.onopen = function (event) {
            pendingMessages.push("Socket connection successful");
            socket.send("join:");
            heartbeatPing();
        };
        socket.onmessage = function (event) {
            if (typeof event.data === "string") {
                if (event.data === "--pong--") {
                    heartbeatPing();
                    return;
                }
                let msg = event.data;
                if (established) {
                    endpoint.viewer.printMessage(msg);
                } else if (msg.startsWith("joined:")) {
                    console.log(msg);
                    let payload = JSON.parse(msg.substring(7));
                    establish(payload);
                }
            }
        };
        socket.onclose = function (event) {
            endpoint.viewer.printEventMessage('Socket connection closed. Please refresh this page to try again!');
            closeSocket();
        };
        socket.onerror = function (event) {
            console.error("WebSocket error observed:", event);
            if (onErrorObserved) {
                onErrorObserved(endpoint);
            } else {
                endpoint.viewer.printErrorMessage('Could not connect to WebSocket server');
                setTimeout(function () {
                    openSocket();
                }, 60000);
            }
        };
    };

    const closeSocket = function () {
        if (socket) {
            socket.close();
            socket = null;
        }
    };

    const establish = function (payload) {
        if (onEndpointJoined) {
            endpoint['mode'] = "websocket";
            onEndpointJoined(endpoint, payload);
        }
        while (pendingMessages.length) {
            endpoint.viewer.printEventMessage(pendingMessages.shift());
        }
        if (onEstablishCompleted) {
            onEstablishCompleted(endpoint, payload);
        }
        while (pendingMessages.length) {
            endpoint.viewer.printEventMessage(pendingMessages.shift());
        }
        established = true;
        socket.send("established:");
    };

    const heartbeatPing = function () {
        if (heartbeatTimer) {
            clearTimeout(heartbeatTimer);
        }
        heartbeatTimer = setTimeout(function () {
            if (socket) {
                socket.send("--ping--");
            }
        }, 57000);
    };
}
