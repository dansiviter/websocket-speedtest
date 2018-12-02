var webSocket = new WebSocket("ws://" + location.host + "/ws");

webSocket.onopen = function(msgEvent) { 
	postMessage({ type: "OPEN" });
}
webSocket.onmessage = function(e) {
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
webSocket.onclose = function(msgEvent) {
	postMessage({ type: "CLOSE" });
}
webSocket.onerror = function(msgEvent) { 
	postMessage({ type: "ERROR" });
}

onmessage = function(e) {
	switch (e.data.type) {
	case "START":
		e.data.clientStart = Date.now();
		webSocket.send(JSON.stringify(e.data));
	}
}