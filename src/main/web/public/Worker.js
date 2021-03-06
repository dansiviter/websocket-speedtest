var webSocket;

onWsOpen = e => {
	postMessage({ type: "OPEN" });
}
onWsMessage = e => {
	var timestamp = Date.now();

	if (typeof e.data === "string") {
		var results = JSON.parse(e.data);
		postMessage({
			type: "RESULTS",
			clientTimestamp: timestamp,
			data: e.data
		});
		return;
	}
	postMessage({ type: "MESSAGE", data: e.data })
}
onWsClose = e => {
	postMessage({ type: "CLOSE" });
}
onWsError = e => {
	postMessage({ type: "ERROR" });
}

onmessage = e => {
	switch (e.data.type) {
	case "START": {
		e.data.clientStart = Date.now();
		webSocket.send(JSON.stringify(e.data));
		break;
	}
	case "RECONNECT": {
		reconnect();
		break;
	}
	}
}

reconnect = () => {
	if (this.webSocket) {
		webSocket.close();
	}

	webSocket = new WebSocket("ws://" + location.host + "/api/v1/ws");
	webSocket.onclose = onWsClose;
	webSocket.onerror = onWsError;
	webSocket.onmessage = onWsMessage;
	webSocket.onopen = onWsOpen;
}

reconnect();
