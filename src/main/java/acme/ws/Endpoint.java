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

import java.io.IOException;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

import acme.PingService;
import acme.api.ControlMessage;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [6 Aug 2018]
 */
@ServerEndpoint(
		value = "/ws",
		subprotocols = "speed-test",
		decoders = { ControlMessageEncoding.class, FileEncoding.class },
		encoders = { ControlMessageEncoding.class, FileEncoding.class, ResultsEncoder.class }
		)
public class Endpoint {
	private static final String SERVICE = "service";

	@Inject
	private Logger log;
	@Inject
	private Provider<PingService> pingService;
	@Resource
	private ManagedScheduledExecutorService executor;

	@OnOpen
	public void onOpen(Session session) {
		this.log.infof("Connection opened. [sessionId=%s]", session.getId());
	}

	@OnMessage
	public void on(Session session, ControlMessage msg) {
		this.log.infof("Control received. [sessionId=%s,type=%s]", session.getId(), msg.type());
		switch (msg.type()) {
		case START: {
			session.getUserProperties().clear();
			pingService(session).start(
					session,
					msg);
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
	public void on(Session session, byte[] msg, boolean last) {
		this.log.infof("Binary received. [sessionId=%s,msg=%s,last=%s]", session.getId(), msg, last);
	}

	@OnMessage
	public void on(Session session, PongMessage msg) {
		final long nanos = System.nanoTime();
		this.log.debugf("Pong received. [sessionId=%s,data=%s]", session.getId(), msg.getApplicationData());
		pingService(session).on(msg, nanos);
	}

	@OnError
	public void onError(Session session, Throwable t) {
		this.log.warnf(t, "Error! [sessionId=%s,msg=%s]", session.getId(), t.getMessage());
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendText("ERROR: " + t.getMessage());
			}
		} catch (IOException e) {
			this.log.error(e.getMessage(), e);
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		this.log.infof("Connection closed. [sessionId=%s,reasonCode=%s]", session.getId(), reason.getCloseCode());
	}

	/**
	 * 
	 * @param session
	 * @return
	 */
	private PingService pingService(Session session) {
		return PingService.class.cast(
				session.getUserProperties().computeIfAbsent(SERVICE, (k) -> this.pingService.get()));
	}
}
