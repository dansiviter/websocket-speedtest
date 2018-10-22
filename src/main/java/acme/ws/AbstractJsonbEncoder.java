package acme.ws;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.EndpointConfig;

public class AbstractJsonbEncoder {
	protected Jsonb jsonb;

	public void init(EndpointConfig config) {
		this.jsonb = JsonbBuilder.create();
	}

	public void destroy() {
		// nothing to see here!
	}
}
