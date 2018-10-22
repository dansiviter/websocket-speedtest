/*
 * Copyright 2016-2018 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package acme.ws;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.websocket.OnMessage;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import acme.SpeedTestService;
import acme.api.ControlMessage;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [6 Aug 2018]
 */
@ServerEndpoint(
		subprotocols = "speed-test",
		value = "/ws",
		decoders = { ControlMessageEncoding.class, FileEncoding.class },
		encoders = { ControlMessageEncoding.class, FileEncoding.class, ResultsEncoder.class }
)
public class Endpoint {
	private static final String SERVICE = "service";

	@Inject
	private Logger log;
	@Inject
	private Provider<SpeedTestService> pingService;
	@Resource
	private ManagedScheduledExecutorService executor;

	@OnMessage
	public void onControl(Session session, ControlMessage msg) {
		this.log.log(INFO, "Control received. [type={0}]", msg.type());
		switch (msg.type()) {
		case START: {
			session.getUserProperties().clear();
			pingService(session).start(session, msg.intParam("warmUp"), msg.intParam("cycles"));
			break;
		}
		case FINISH: {
			// return the results
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown type!");
		}
	}

	@OnMessage
	public void onBinary(Session session, byte[] msg, boolean last) {
		this.log.log(INFO, "Binary received. [msg={0},last={1}]", new Object[] { msg, last });
	}

	@OnMessage
	public void onPing(Session session, PongMessage msg) {
		this.log.log(FINE, "Ping received. [{0}]", msg);

		if (msg.getApplicationData().capacity() == 0) { // warmup, ignore
			return;
		}

		final long nanos = System.nanoTime();
		pingService(session).onPing(msg, nanos);
	}

	/**
	 * 
	 * @param session
	 * @return
	 */
	private SpeedTestService pingService(Session session) {
		return SpeedTestService.class.cast(
				session.getUserProperties().computeIfAbsent(SERVICE, (k) -> this.pingService.get()));
	}
}
