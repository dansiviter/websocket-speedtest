/*
 * Copyright 2018-2020 Daniel Siviter
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
package uk.dansiviter.ws;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import uk.dansiviter.Log;
import uk.dansiviter.PingService;
import uk.dansiviter.api.ControlMessage;

/**
 *
 * @author Daniel Siviter
 * @since v1.0 [6 Aug 2018]
 */
public class WsEndpoint extends Endpoint {
	private static final String SERVICE = "service";

	@Inject
	private Log log;
	@Inject
	private Provider<PingService> pingService;

	private Session session;

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		this.log.infof("Connection opened. [sessionId=%s]", session.getId());
		session.addMessageHandler(ControlMessage.class, m -> on(m));
		session.addMessageHandler(PongMessage.class, m -> on(m));
	}

	void on(ControlMessage msg) {
		this.log.infof("Control received. [sessionId=%s,type=%s]", session.getId(), msg.type());
		switch (msg.type()) {
		case START: {
			session.getUserProperties().clear();
			pingService(session).start(
					session,
					msg);
			break;
		}
		case FINISH:
			break;  // return the results
		default:
			throw new IllegalArgumentException("Unknown type!");
		}
	}

	void on(PongMessage msg) {
		final long nanos = System.nanoTime();
		this.log.debugf("Pong received. [sessionId=%s,data=%s]", this.session.getId(), msg.getApplicationData());
		pingService(session).on(msg, nanos);
	}

	@Override
	public void onError(Session session, Throwable t) {
		this.log.warnf(t, "Error! [sessionId=%s,msg=%s]", this.session.getId(), t.getMessage());
		try {
			if (this.session.isOpen()) {
				this.session.getBasicRemote().sendText("ERROR: " + t.getMessage());
			}
		} catch (IOException e) {
			this.log.error(e.getMessage(), e);
		}
	}

	@Override
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
				session.getUserProperties().computeIfAbsent(SERVICE, k -> this.pingService.get()));
	}
}
